package com.youyi.craft.job.once;

import com.youyi.craft.service.GeneratorService;
import java.util.List;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

/**
 * 热点代码生成器缓存预热
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
@Slf4j
@Component
public class HotGeneratorCacheWarmupInitializer implements InitializingBean {

    @Resource
    private GeneratorService generatorService;

    @Override
    public void afterPropertiesSet() throws Exception {
        // 缓存热点代码生成器
        List<Long> idList = generatorService.listHotGeneratorIds();
        generatorService.cacheGenerators(idList);
        log.info("cache hot generator ids: {}", idList);
    }
}
