package com.youyi.craft.job.once;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youyi.craft.manager.CacheManager;
import com.youyi.craft.model.dto.generator.GeneratorQueryRequest;
import com.youyi.craft.model.vo.GeneratorVO;
import com.youyi.craft.service.GeneratorService;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

/**
 * 首页数据缓存预热
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
@Slf4j
@Component
public class IndexPageCacheWarmupInitializer implements InitializingBean {

    @Resource
    private CacheManager cacheManager;

    @Resource
    private GeneratorService generatorService;

    @Override
    public void afterPropertiesSet() throws Exception {
        GeneratorQueryRequest defaultReq = new GeneratorQueryRequest();
        defaultReq.setCurrent(1);
        defaultReq.setPageSize(12);
        defaultReq.setSortField("updateTime");
        defaultReq.setSortOrder("descend");

        Page<GeneratorVO> generatorVOPage = generatorService.listGeneratorVOByPageSimplifyData(
                defaultReq);

        String cacheKey = cacheManager.getPageCacheKey(defaultReq);
        cacheManager.put(cacheKey, generatorVOPage);
        log.info("index page cache warmup completed");
    }
}
