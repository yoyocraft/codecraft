package com.youyi.craft.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.model.COSObjectInputStream;
import com.qcloud.cos.utils.IOUtils;
import com.youyi.craft.annotation.AuthCheck;
import com.youyi.craft.common.BaseResponse;
import com.youyi.craft.common.DeleteRequest;
import com.youyi.craft.common.ErrorCode;
import com.youyi.craft.common.ResultUtils;
import com.youyi.craft.constant.UserConstant;
import com.youyi.craft.exception.BusinessException;
import com.youyi.craft.exception.ThrowUtils;
import com.youyi.craft.manager.CosManager;
import com.youyi.craft.manager.LocalFileCacheManager;
import com.youyi.craft.model.dto.generator.GeneratorAddRequest;
import com.youyi.craft.model.dto.generator.GeneratorCacheRequest;
import com.youyi.craft.model.dto.generator.GeneratorDelCacheRequest;
import com.youyi.craft.model.dto.generator.GeneratorEditRequest;
import com.youyi.craft.model.dto.generator.GeneratorMakeRequest;
import com.youyi.craft.model.dto.generator.GeneratorQueryRequest;
import com.youyi.craft.model.dto.generator.GeneratorUpdateRequest;
import com.youyi.craft.model.dto.generator.GeneratorUseRequest;
import com.youyi.craft.model.entity.Generator;
import com.youyi.craft.model.entity.User;
import com.youyi.craft.model.vo.GeneratorVO;
import com.youyi.craft.service.GeneratorService;
import com.youyi.craft.service.UserService;
import io.github.dingxinliang88.maker.generator.main.GeneratorTemplate;
import io.github.dingxinliang88.maker.generator.main.SrcZipGenerator;
import io.github.dingxinliang88.maker.generator.main.ZipGenerator;
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
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 生成器接口
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
@RestController
@RequestMapping("/generator")
@Slf4j
public class GeneratorController {

    @Resource
    private GeneratorService generatorService;

    @Resource
    private UserService userService;

    @Resource
    private CosManager cosManager;

    // region 增删改查

    /**
     * 创建
     *
     * @param generatorAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addGenerator(@RequestBody GeneratorAddRequest generatorAddRequest,
            HttpServletRequest request) {
        if (generatorAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Generator generator = new Generator();
        BeanUtils.copyProperties(generatorAddRequest, generator);
        List<String> tags = generatorAddRequest.getTags();
        generator.setTags(JSONUtil.toJsonStr(tags));
        Meta.FileConfig fileConfig = generatorAddRequest.getFileConfig();
        generator.setFileConfig(JSONUtil.toJsonStr(fileConfig));
        Meta.ModelConfig modelConfig = generatorAddRequest.getModelConfig();
        generator.setModelConfig(JSONUtil.toJsonStr(modelConfig));

        // 参数校验
        generatorService.validGenerator(generator, true);
        User loginUser = userService.getLoginUser(request);
        generator.setUserId(loginUser.getId());
        generator.setStatus(0);
        boolean result = generatorService.save(generator);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newGeneratorId = generator.getId();
        return ResultUtils.success(newGeneratorId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteGenerator(@RequestBody DeleteRequest deleteRequest,
            HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        Generator oldGenerator = generatorService.getById(id);
        ThrowUtils.throwIf(oldGenerator == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldGenerator.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = generatorService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新（仅管理员）
     *
     * @param generatorUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateGenerator(
            @RequestBody GeneratorUpdateRequest generatorUpdateRequest) {
        if (generatorUpdateRequest == null || generatorUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Generator generator = new Generator();
        BeanUtils.copyProperties(generatorUpdateRequest, generator);
        List<String> tags = generatorUpdateRequest.getTags();
        generator.setTags(JSONUtil.toJsonStr(tags));
        Meta.FileConfig fileConfig = generatorUpdateRequest.getFileConfig();
        generator.setFileConfig(JSONUtil.toJsonStr(fileConfig));
        Meta.ModelConfig modelConfig = generatorUpdateRequest.getModelConfig();
        generator.setModelConfig(JSONUtil.toJsonStr(modelConfig));

        // 参数校验
        generatorService.validGenerator(generator, false);
        long id = generatorUpdateRequest.getId();
        // 判断是否存在
        Generator oldGenerator = generatorService.getById(id);
        ThrowUtils.throwIf(oldGenerator == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = generatorService.updateById(generator);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<GeneratorVO> getGeneratorVOById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Generator generator = generatorService.getById(id);
        if (generator == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(generatorService.getGeneratorVO(generator, request));
    }

    /**
     * 分页获取列表（仅管理员）
     *
     * @param generatorQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<Generator>> listGeneratorByPage(
            @RequestBody GeneratorQueryRequest generatorQueryRequest) {
        long current = generatorQueryRequest.getCurrent();
        long size = generatorQueryRequest.getPageSize();
        Page<Generator> generatorPage = generatorService.page(new Page<>(current, size),
                generatorService.getQueryWrapper(generatorQueryRequest));
        return ResultUtils.success(generatorPage);
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param generatorQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<GeneratorVO>> listGeneratorVOByPage(
            @RequestBody GeneratorQueryRequest generatorQueryRequest,
            HttpServletRequest request) {
        long current = generatorQueryRequest.getCurrent();
        long size = generatorQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);

        StopWatch stopWatch = new StopWatch("分页查询生成器");
        stopWatch.start("查询生成器");
        Page<Generator> generatorPage = generatorService.page(new Page<>(current, size),
                generatorService.getQueryWrapper(generatorQueryRequest));
        stopWatch.stop();

        stopWatch.start("关联查询信息");
        Page<GeneratorVO> generatorVOPage = generatorService.getGeneratorVOPage(generatorPage,
                request);
        stopWatch.stop();

        // 打印测试结果
        System.out.println(stopWatch.prettyPrint());
        return ResultUtils.success(generatorVOPage);
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param generatorQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo/v2")
    public BaseResponse<Page<GeneratorVO>> listGeneratorVOByPageSimplifyData(
            @RequestBody GeneratorQueryRequest generatorQueryRequest,
            HttpServletRequest request) {
        long current = generatorQueryRequest.getCurrent();
        long size = generatorQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);

        QueryWrapper<Generator> queryWrapper = generatorService.getQueryWrapper(
                generatorQueryRequest);
        queryWrapper.select("id", "name", "description", "author", "tags",
                "picture", "userId", "createTime", "updateTime");
        Page<Generator> generatorPage = generatorService.page(new Page<>(current, size),
                queryWrapper);
        return ResultUtils.success(generatorService.getGeneratorVOPage(generatorPage,
                request));
    }

    /**
     * 分页获取当前用户创建的资源列表
     *
     * @param generatorQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<GeneratorVO>> listMyGeneratorVOByPage(
            @RequestBody GeneratorQueryRequest generatorQueryRequest,
            HttpServletRequest request) {
        if (generatorQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        generatorQueryRequest.setUserId(loginUser.getId());
        long current = generatorQueryRequest.getCurrent();
        long size = generatorQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Generator> generatorPage = generatorService.page(new Page<>(current, size),
                generatorService.getQueryWrapper(generatorQueryRequest));
        return ResultUtils.success(generatorService.getGeneratorVOPage(generatorPage, request));
    }

    // endregion

    /**
     * 编辑（用户）
     *
     * @param generatorEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editGenerator(
            @RequestBody GeneratorEditRequest generatorEditRequest, HttpServletRequest request) {
        if (generatorEditRequest == null || generatorEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Generator generator = new Generator();
        BeanUtils.copyProperties(generatorEditRequest, generator);
        List<String> tags = generatorEditRequest.getTags();
        generator.setTags(JSONUtil.toJsonStr(tags));
        Meta.FileConfig fileConfig = generatorEditRequest.getFileConfig();
        generator.setFileConfig(JSONUtil.toJsonStr(fileConfig));
        Meta.ModelConfig modelConfig = generatorEditRequest.getModelConfig();
        generator.setModelConfig(JSONUtil.toJsonStr(modelConfig));

        // 参数校验
        generatorService.validGenerator(generator, false);
        User loginUser = userService.getLoginUser(request);
        long id = generatorEditRequest.getId();
        // 判断是否存在
        Generator oldGenerator = generatorService.getById(id);
        ThrowUtils.throwIf(oldGenerator == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        if (!oldGenerator.getUserId().equals(loginUser.getId()) && !userService.isAdmin(
                loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = generatorService.updateById(generator);
        if (result) {
            String cacheFilePath = LocalFileCacheManager.getCacheFilePath(id,
                    generatorEditRequest.getDistPath());
            // 删除缓存
            FileUtil.del(cacheFilePath);
        }
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 下载
     *
     * @param id
     */
    @GetMapping("/download")
    public void downloadGeneratorById(Long id, HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        User loginUser = userService.getLoginUser(request);
        Generator generator = generatorService.getById(id);
        if (generator == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }

        String filepath = generator.getDistPath();
        if (StrUtil.isBlank(filepath)) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "产物包路径不存在");
        }

        // 追踪事件
        log.info("user {} download {}", loginUser, filepath);

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
        }
    }

    /**
     * 在线使用生成器
     *
     * @param generatorUseRequest
     * @param request
     * @param response
     */
    @PostMapping("/use")
    public void onlineUseGenerator(@RequestBody GeneratorUseRequest generatorUseRequest,
            HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 获取用户的输入参数
        Long id = generatorUseRequest.getId();
        Map<String, Object> dataModel = generatorUseRequest.getDataModel();

        // 需要用户登录
        User loginUser = userService.getLoginUser(request);
        log.info("userId: {} use generator, generatorId: {}", loginUser.getId(), id);

        // 获取到生成器存储路径
        Generator generator = generatorService.getById(id);
        if (Objects.isNull(generator)) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        String distPath = generator.getDistPath();
        if (StrUtil.isBlank(distPath)) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "产物包不存在");
        }

        // 定义独立的工作空间
        String projectPath = System.getProperty("user.dir");
        String tmpDirPath = String.format("%s/.tmp/use/%s/%s", projectPath, loginUser.getId(), id);
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
        File scriptDir = scriptFile.getParentFile();
        String scriptAbsolutePath = scriptFile.getAbsolutePath().replace("\\", "/");
        String[] commands = new String[]{scriptAbsolutePath, "json-generate",
                "--file=" + dataModelFilePath};

        // 执行命令
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
        // TODO 设置线程池
        CompletableFuture.runAsync(() -> FileUtil.del(tmpDirPath));
    }

    /**
     * 在线制作生成器
     *
     * @param generatorMakeRequest
     * @param request
     * @param response
     */
    @PostMapping("/make")
    public void onlineMakeGenerator(@RequestBody GeneratorMakeRequest generatorMakeRequest,
            HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 获取用户输入参数
        String zipFilePath = generatorMakeRequest.getZipFilePath();
        Meta meta = generatorMakeRequest.getMeta();

        // 需要用户登录
        User loginUser = userService.getLoginUser(request);
        log.info("userId: {} make generator, zipFilePath: {}", loginUser.getId(), zipFilePath);

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
            // TODO 异步化操作
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
        CompletableFuture.runAsync(() -> FileUtil.del(tmpDirPath));
    }

    /**
     * 缓存代码生成器
     *
     * @param generatorCacheRequest
     */
    @PostMapping("/cache")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public void cacheGenerator(@RequestBody GeneratorCacheRequest generatorCacheRequest) {
        // TODO 设置热点阈值，比如生成器的使用次数，通过定时任务或者每次下载之后判断
        if (Objects.isNull(generatorCacheRequest) || Objects.isNull(generatorCacheRequest.getId())
                || generatorCacheRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long id = generatorCacheRequest.getId();

        Generator generator = generatorService.getById(id);
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

    @DeleteMapping("/del/cache")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public void delCache(@RequestBody GeneratorDelCacheRequest generatorDelCacheRequest) {
        if (Objects.isNull(generatorDelCacheRequest) || CollUtil.isEmpty(
                generatorDelCacheRequest.getIds())) {
            LocalFileCacheManager.clearExpireCache();
            return;
        }

        List<Long> ids = generatorDelCacheRequest.getIds();
        List<Generator> generatorList = generatorService.getBatchByIds(ids);
        List<String> cacheKeyList = generatorList.stream()
                .filter(generator -> StrUtil.isNotBlank(generator.getDistPath()))
                .map(generator -> LocalFileCacheManager.getCacheFilePath(generator.getId(),
                        generator.getDistPath()))
                .collect(Collectors.toList());
        LocalFileCacheManager.clearCache(cacheKeyList);
    }

}
