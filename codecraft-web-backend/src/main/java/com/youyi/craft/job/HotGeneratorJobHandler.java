package com.youyi.craft.job;

import com.youyi.craft.service.GeneratorService;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 热点代码生成器定时任务
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
@Component
public class HotGeneratorJobHandler {

    @Resource
    private GeneratorService generatorService;


    @Scheduled(cron = "0 0 1 * * ?") // 每天凌晨1点执行
    public void cacheHotGenerators() {
        List<Long> idList = generatorService.listHotGeneratorIds();
        generatorService.cacheGenerators(idList);
    }


}
