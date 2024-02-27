package com.youyi.craft.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.util.concurrent.RateLimiter;
import com.youyi.craft.annotation.AuthCheck;
import com.youyi.craft.common.BaseResponse;
import com.youyi.craft.common.DeleteRequest;
import com.youyi.craft.common.ErrorCode;
import com.youyi.craft.common.ResultUtils;
import com.youyi.craft.constant.UserConstant;
import com.youyi.craft.exception.BusinessException;
import com.youyi.craft.exception.ThrowUtils;
import com.youyi.craft.manager.CacheManager;
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
import io.github.dingxinliang88.maker.meta.Meta;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
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
    private CacheManager cacheManager;

    private static final RateLimiter USE_LIMITER = RateLimiter.create(10);
    private static final RateLimiter MAKE_LIMITER = RateLimiter.create(10);

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
        User loginUser = userService.getLoginUser(request);

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
     * @deprecated
     */
    @Deprecated
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<GeneratorVO>> listGeneratorVOByPage(
            @RequestBody GeneratorQueryRequest generatorQueryRequest,
            HttpServletRequest request) {
        long current = generatorQueryRequest.getCurrent();
        long size = generatorQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);

        Page<Generator> generatorPage = generatorService.page(new Page<>(current, size),
                generatorService.getQueryWrapper(generatorQueryRequest));
        return ResultUtils.success(generatorService.getGeneratorVOPage(generatorPage));
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
        long size = generatorQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);

        // 优先从缓存获取
        String cacheKey = cacheManager.getPageCacheKey(generatorQueryRequest);
        Object cache = cacheManager.get(cacheKey);
        if (Objects.nonNull(cache)) {
            // noinspection unchecked
            return ResultUtils.success((Page<GeneratorVO>) cache);
        }
        Page<GeneratorVO> generatorVOPage = generatorService.listGeneratorVOByPageSimplifyData(
                generatorQueryRequest);
        // 写入缓存
        cacheManager.put(cacheKey, generatorVOPage);
        return ResultUtils.success(generatorVOPage);
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
        return ResultUtils.success(generatorService.getGeneratorVOPage(generatorPage));
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
     * @param id 生成器id
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

        generatorService.downloadGenerator(generator, response);
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
        if (!USE_LIMITER.tryAcquire()) {
            throw new BusinessException(ErrorCode.TOO_MANY_REQUEST);
        }
        // 获取用户的输入参数
        Long id = generatorUseRequest.getId();
        Map<String, Object> dataModel = generatorUseRequest.getDataModel();
        if (ObjectUtil.isEmpty(dataModel)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

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

        generatorService.onlineUseGenerator(generator, dataModel, loginUser.getId(), response);
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
        if (!MAKE_LIMITER.tryAcquire()) {
            throw new BusinessException(ErrorCode.TOO_MANY_REQUEST);
        }
        // 获取用户输入参数
        String zipFilePath = generatorMakeRequest.getZipFilePath();
        Meta meta = generatorMakeRequest.getMeta();

        // 需要用户登录
        User loginUser = userService.getLoginUser(request);
        log.info("userId: {} make generator, zipFilePath: {}", loginUser.getId(), zipFilePath);

        generatorService.onlineMakerGenerator(meta, zipFilePath, response);
    }


    /**
     * 在线制作生成器，直接上传到后端，不上传文件到对象存储
     *
     * @param generatorMakeRequest
     * @param request
     * @param response
     */
    @PostMapping("/make/v2")
    @Deprecated
    public void onlineMakeGeneratorWithoutCos(
            @RequestBody GeneratorMakeRequest generatorMakeRequest,
            HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!MAKE_LIMITER.tryAcquire()) {
            throw new BusinessException(ErrorCode.TOO_MANY_REQUEST);
        }

        // 需要用户登录
        User loginUser = userService.getLoginUser(request);
        log.info("userId: {} make generator", loginUser.getId());

        // do nothing
    }

    /**
     * 缓存代码生成器
     */
    @PostMapping("/cache")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public void cacheGenerator(@RequestBody GeneratorCacheRequest generatorCacheRequest) {
        List<Long> idList = generatorCacheRequest.getIdList();
        if (CollUtil.isEmpty(idList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        generatorService.cacheGenerators(idList);
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

    // TODO 完善生成器状态流转，考虑使用状态机
    // TODO 推荐生成器（根据标签 余弦相似度算法）

}
