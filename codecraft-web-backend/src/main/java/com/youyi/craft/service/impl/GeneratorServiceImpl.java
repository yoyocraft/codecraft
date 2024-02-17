package com.youyi.craft.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.model.COSObjectInputStream;
import com.qcloud.cos.utils.IOUtils;
import com.youyi.craft.common.ErrorCode;
import com.youyi.craft.constant.CommonConstant;
import com.youyi.craft.constant.GeneratorConstant;
import com.youyi.craft.exception.BusinessException;
import com.youyi.craft.exception.ThrowUtils;
import com.youyi.craft.manager.CosManager;
import com.youyi.craft.manager.LocalFileCacheManager;
import com.youyi.craft.mapper.GeneratorMapper;
import com.youyi.craft.model.dto.generator.GeneratorQueryRequest;
import com.youyi.craft.model.entity.Generator;
import com.youyi.craft.model.entity.User;
import com.youyi.craft.model.vo.GeneratorVO;
import com.youyi.craft.model.vo.UserVO;
import com.youyi.craft.service.GeneratorService;
import com.youyi.craft.service.UserService;
import com.youyi.craft.utils.SqlUtils;
import io.github.dingxinliang88.maker.generator.main.GeneratorTemplate;
import io.github.dingxinliang88.maker.generator.main.SrcZipGenerator;
import io.github.dingxinliang88.maker.meta.Meta;
import io.github.dingxinliang88.maker.meta.MetaValidator;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
@Slf4j
@Service
public class GeneratorServiceImpl extends ServiceImpl<GeneratorMapper, Generator>
        implements GeneratorService {

    @Resource
    private GeneratorMapper generatorMapper;

    @Resource
    private UserService userService;

    @Resource
    private CosManager cosManager;

    private static final ExecutorService CLEAN_UP_POOL = new ThreadPoolExecutor(
            1,
            5,
            60L,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(100),
            r -> new Thread(r, "clean-up-thread"),
            new ThreadPoolExecutor.AbortPolicy()
    );

    private static final ExecutorService INCR_COUNT_POOL = new ThreadPoolExecutor(
            1,
            5,
            60L,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(100),
            r -> new Thread(r, "clean-up-thread"),
            new ThreadPoolExecutor.AbortPolicy()
    );

    @Override
    public void validGenerator(Generator generator, boolean add) {
        if (generator == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String name = generator.getName();
        String description = generator.getDescription();
        // 创建时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(StringUtils.isAnyBlank(name, description),
                    ErrorCode.PARAMS_ERROR);
        }
        // 有参数则校验
        if (StringUtils.isNotBlank(name) && name.length() > 80) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "名称过长");
        }
        if (StringUtils.isNotBlank(description) && description.length() > 512) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "描述过长");
        }
    }

    /**
     * 获取查询包装类
     *
     * @param generatorQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<Generator> getQueryWrapper(GeneratorQueryRequest generatorQueryRequest) {
        QueryWrapper<Generator> queryWrapper = new QueryWrapper<>();
        if (generatorQueryRequest == null) {
            return queryWrapper;
        }
        String searchText = generatorQueryRequest.getSearchText();
        String sortField = generatorQueryRequest.getSortField();
        String sortOrder = generatorQueryRequest.getSortOrder();
        Long id = generatorQueryRequest.getId();
        String name = generatorQueryRequest.getName();
        String description = generatorQueryRequest.getDescription();
        List<String> tagList = generatorQueryRequest.getTags();
        Long userId = generatorQueryRequest.getUserId();
        Long notId = generatorQueryRequest.getNotId();
        Integer status = generatorQueryRequest.getStatus();
        String author = generatorQueryRequest.getAuthor();
        String version = generatorQueryRequest.getVersion();

        // 拼接查询条件
        if (StringUtils.isNotBlank(searchText)) {
            queryWrapper.and(qw -> qw.like("name", searchText)
                    .or()
                    .like("description", searchText));
        }
        queryWrapper.like(StringUtils.isNotBlank(name), "name", name);
        queryWrapper.like(StringUtils.isNotBlank(description), "description", description);
        if (CollUtil.isNotEmpty(tagList)) {
            for (String tag : tagList) {
                queryWrapper.like("tags", "\"" + tag + "\"");
            }
        }
        queryWrapper.eq(ObjectUtils.isNotEmpty(status), "status", status);
        queryWrapper.eq(StringUtils.isNotBlank(author), "author", author);
        queryWrapper.eq(StringUtils.isNotBlank(version), "version", version);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.ne(ObjectUtils.isNotEmpty(notId), "id", notId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }


    @Override
    public GeneratorVO getGeneratorVO(Generator generator, HttpServletRequest request) {
        GeneratorVO generatorVO = GeneratorVO.objToVo(generator);
        if (Objects.isNull(generatorVO)) {
            return null;
        }
        // 关联查询用户信息
        Long userId = generator.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        generatorVO.setUser(userVO);

        return generatorVO;
    }

    @Override
    public Page<GeneratorVO> getGeneratorVOPage(Page<Generator> generatorPage,
            HttpServletRequest request) {
        List<Generator> generatorList = generatorPage.getRecords();
        Page<GeneratorVO> generatorVOPage = new Page<>(generatorPage.getCurrent(),
                generatorPage.getSize(),
                generatorPage.getTotal());
        if (CollUtil.isEmpty(generatorList)) {
            return generatorVOPage;
        }
        // 1. 关联查询用户信息
        Set<Long> userIdSet = generatorList.stream().map(Generator::getUserId)
                .collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        // 填充信息
        List<GeneratorVO> generatorVOList = generatorList.stream().map(generator -> {
            GeneratorVO generatorVO = GeneratorVO.objToVo(generator);
            if (Objects.isNull(generatorVO)) {
                return null;
            }
            Long userId = generator.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            generatorVO.setUser(userService.getUserVO(user));
            return generatorVO;
        }).collect(Collectors.toList());
        generatorVOPage.setRecords(generatorVOList);
        return generatorVOPage;
    }

    @Override
    public List<Generator> getBatchByIds(List<Long> idList) {
        if (CollUtil.isEmpty(idList)) {
            return Collections.emptyList();
        }
        return generatorMapper.selectBatchIds(idList);
    }

    @Override
    public void cacheGenerators(List<Long> idList) {
        for (Long id : idList) {
            if (id <= 0) {
                continue;
            }
            log.info("cache generator, id = {}", id);
            Generator generator = this.getById(id);
            if (Objects.isNull(generator)) {
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
            }
            String distPath = generator.getDistPath();
            if (StrUtil.isBlank(distPath)) {
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "产物包不存在");
            }

            String zipFilePath = LocalFileCacheManager.getCacheFilePath(id, distPath);

            if (FileUtil.exist(zipFilePath)) {
                FileUtil.del(zipFilePath);
            }
            FileUtil.touch(zipFilePath);

            try {
                cosManager.download(distPath, zipFilePath);
                // 给缓存设置过期时间
                LocalFileCacheManager.updateCacheExpiration(zipFilePath);
            } catch (InterruptedException e) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "生成器下载失败");
            }
        }
    }

    @Override
    public List<Long> listHotGeneratorIds() {
        return generatorMapper.listHotGeneratorIds();
    }

    @Override
    public void downloadGenerator(Generator generator, HttpServletResponse response)
            throws IOException {
        String filepath = generator.getDistPath();
        Long id = generator.getId();
        // 设置响应头
        response.setContentType("application/octet-stream;charSet=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=" + filepath);

        // 查询本地缓存
        String zipFilePath = LocalFileCacheManager.getCacheFilePath(id, generator.getDistPath());
        if (FileUtil.exist(zipFilePath)) {
            log.info("download generator from cache, id = {}", id);
            // 从缓存下载
            Files.copy(Paths.get(zipFilePath), response.getOutputStream());
            return;
        }

        // 从对象存储下载
        COSObjectInputStream cosObjectInput = null;
        try {
            COSObject cosObject = cosManager.getObject(filepath);
            cosObjectInput = cosObject.getObjectContent();
            // 处理下载到的流
            byte[] bytes = IOUtils.toByteArray(cosObjectInput);

            // 写入响应
            response.getOutputStream().write(bytes);
            response.getOutputStream().flush();
        } catch (Exception e) {
            log.error("file download error, filepath = " + filepath, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "下载失败");
        } finally {
            if (cosObjectInput != null) {
                cosObjectInput.close();
            }
            CompletableFuture.runAsync(() -> incrDownloadCount(generator), INCR_COUNT_POOL);
        }
    }

    @Override
    public void onlineUseGenerator(Generator generator, Object dataModel, Long userId,
            HttpServletResponse response) throws IOException {
        String distPath = generator.getDistPath();
        Long id = generator.getId();

        // 定义独立的工作空间，用户单独的工作空间
        String projectPath = System.getProperty("user.dir");
        String tmpDirPath = String.format("%s/.tmp/use/%s/%s", projectPath, userId, id);
        String zipFilePath = tmpDirPath + "/dist.zip";

        if (!FileUtil.exist(zipFilePath)) {
            FileUtil.touch(zipFilePath);
        }

        String cacheFilePath = LocalFileCacheManager.getCacheFilePath(id, distPath);
        // 判断当前要执行的生成器是否在缓存中
        if (LocalFileCacheManager.isCached(cacheFilePath)) {
            // 复制
            FileUtil.copy(cacheFilePath, zipFilePath, true);
        } else {
            // 从对象存储中下载生成器压缩包
            try {
                cosManager.download(distPath, zipFilePath);
            } catch (InterruptedException e) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "生成器下载失败");
            }
        }

        // 解压压缩包，得到脚本文件
        File unzipDistDir = ZipUtil.unzip(zipFilePath);

        // 将用户输入的参数写入到 json 文件中
        String dataModelFilePath = tmpDirPath + "/dataModel.json";
        String dataModelJsonStr = JSONUtil.toJsonStr(dataModel);
        FileUtil.writeUtf8String(dataModelJsonStr, dataModelFilePath);

        // 执行脚本
        // 查找脚本路径，注意区分系统
        String os = System.getProperty("os.name");
        boolean isWin = os.toLowerCase().contains("windows");
        String scriptFileName = isWin ? "craft.bat" : "craft";
        File scriptFile = FileUtil.loopFiles(unzipDistDir, 2, null)
                .stream()
                .filter(file -> file.isFile() && scriptFileName.equals(file.getName()))
                .findFirst()
                .orElseThrow(
                        () -> new BusinessException(ErrorCode.NOT_FOUND_ERROR,
                                "脚本文件 " + scriptFileName + "不存在"));

        // 添加执行权限
        try {
            // 赋予执行权限
            Set<PosixFilePermission> permissions = PosixFilePermissions.fromString("rwxrwxrwx");
            Files.setPosixFilePermissions(scriptFile.toPath(), permissions);
        } catch (IOException ignored) {
        }

        // 构造命令
        String scriptAbsolutePath = scriptFile.getAbsolutePath().replace("\\", "/");
        String[] commands = {scriptAbsolutePath, "json-generate", "--file=" + dataModelFilePath};

        // 执行命令
        File scriptDir = scriptFile.getParentFile();
        ProcessBuilder processBuilder = new ProcessBuilder(commands);
        processBuilder.directory(scriptDir);

        try {
            Process process = processBuilder.start();

            // 读取命令的输出
            InputStream inputStream = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                log.info(line);
            }
            int exitCode = process.waitFor();
            log.info("execute script finished! exit code = {}", exitCode);
        } catch (Exception e) {
            log.error("execute script error, ", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "脚本执行失败");
        }

        // 压缩得到生成的结果，写入给前端
        String generatedPath = scriptDir.getAbsolutePath() + "/generated";
        String resultPath = tmpDirPath + "/result.zip";
        File resultFile = ZipUtil.zip(generatedPath, resultPath);

        // 设置响应头
        response.setContentType("application/octet-stream;charSet=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=" + resultFile.getName());
        Files.copy(resultFile.toPath(), response.getOutputStream());

        // 清理文件
        CompletableFuture.runAsync(() -> FileUtil.del(tmpDirPath), CLEAN_UP_POOL);
        // 使用次数和阈值判断
        CompletableFuture.runAsync(() -> {
            Integer useCount = generator.getUseCount();
            if (useCount >= GeneratorConstant.HOT_GENERATOR_USE_COUNT_THRESHOLD - 1) {
                log.info("cache generator, id = {}", generator.getId());
                this.cacheGenerators(Collections.singletonList(id));
            }
            // 更新使用次数
            incrUseCount(generator);
        }, INCR_COUNT_POOL);
    }

    @Override
    public void onlineMakerGenerator(Meta meta, String zipFilePath, HttpServletResponse response)
            throws IOException {
        // 创建独立的工作空间，将文件下载到本地
        String projectPath = System.getProperty("user.dir");
        String tmpId = IdUtil.getSnowflakeNextIdStr() + RandomUtil.randomString(6);
        String tmpDirPath = String.format("%s/.tmp/make/%s", projectPath, tmpId);
        String localZipFilePath = tmpDirPath + "/project.zip";

        if (!FileUtil.exist(localZipFilePath)) {
            FileUtil.touch(localZipFilePath);
        }

        // 下载文件
        try {
            cosManager.download(zipFilePath, localZipFilePath);
        } catch (InterruptedException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "项目文件下载失败");
        }

        // 解压，得到项目文件
        File unzipFilePath = ZipUtil.unzip(localZipFilePath);

        // 构造 meta 对象和生成器输出路径
        String sourceRootPath = unzipFilePath.getAbsolutePath();
        meta.getFileConfig().setSourceRootPath(sourceRootPath);
        // 校验和处理默认值
        MetaValidator.doValidateAndFill(meta);
        String outputPath = tmpDirPath + "/generated/" + meta.getName();

        // 调用 maker 制作生成器
        GeneratorTemplate generatorTemplate = new SrcZipGenerator();
        try {
            // 将下载好的生成器写回前端，注意是要完整的文件打包，因为用户可能还会修改
            generatorTemplate.doGenerate(meta, outputPath);
        } catch (Exception e) {
            log.error("make generator failed, ", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "制作失败");
        }

        String zipSuffix = "-dist.zip";
        String zipFileName = meta.getName() + zipSuffix;
        String distZipFilePath = outputPath + zipSuffix;
        // 设置响应头
        response.setContentType("application/octet-stream;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=" + zipFileName);
        Files.copy(Paths.get(distZipFilePath), response.getOutputStream());

        // 清理工作空间
        CompletableFuture.runAsync(() -> FileUtil.del(tmpDirPath), CLEAN_UP_POOL);
    }

    private void incrDownloadCount(Generator generator) {
        generator.setDownloadCount(generator.getDownloadCount() + 1);
        this.updateById(generator);
    }

    private void incrUseCount(Generator generator) {
        generator.setUseCount(generator.getUseCount() + 1);
        this.updateById(generator);
    }

}




