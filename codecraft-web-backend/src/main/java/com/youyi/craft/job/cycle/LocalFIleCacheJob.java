package com.youyi.craft.job.cycle;

import com.youyi.craft.manager.LocalFileCacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 本地文件缓存任务
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
@Component
public class LocalFIleCacheJob {

    /**
     * 一周一次
     */
    @Scheduled(fixedDelay = 60 * 60 * 24 * 7)
    public void clearCache() {
        LocalFileCacheManager.clearExpireCache();
    }

}
