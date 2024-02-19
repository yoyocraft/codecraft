package com.youyi.craft.manager;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Scheduler;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 本地文件缓存管理器
 * <p>
 * 本地文件有一定限度（缓存10个），采用 LRU 算法，主动淘汰掉最不经常使用的
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
public class LocalFileCacheManager {

    /**
     * 本地缓存最大上限 10个
     */
    private static final int CACHE_MAX_COUNT = 10;

    private static final Cache<String, Long> CACHE_EXPIRATION_MAP = Caffeine.newBuilder()
            .expireAfterAccess(24, TimeUnit.HOURS) // 缓存过期时间 1天
            .maximumSize(CACHE_MAX_COUNT) // 本地缓存最大上限 10个
            .scheduler(Scheduler.systemScheduler())
            .build();

    public static synchronized void updateCacheExpiration(String cacheFilePath) {
        CACHE_EXPIRATION_MAP.put(cacheFilePath, System.currentTimeMillis());
    }

    public static boolean isCacheExpired(String cacheFilePath) {
        Long expirationTime = CACHE_EXPIRATION_MAP.getIfPresent(cacheFilePath);
        return expirationTime == null
                || expirationTime + TimeUnit.DAYS.toMillis(1) < System.currentTimeMillis();
    }

    public static void clearExpireCache() {
        List<String> expiredCacheFilePaths = CACHE_EXPIRATION_MAP.asMap().keySet().stream()
                .filter(LocalFileCacheManager::isCacheExpired)
                .toList();
        clearCache(expiredCacheFilePaths);
    }

    public static synchronized void clearCache(List<String> cacheKeyList) {
        if (CollUtil.isEmpty(cacheKeyList)) {
            return;
        }
        for (String cacheKey : cacheKeyList) {
            FileUtil.del(cacheKey);
            CACHE_EXPIRATION_MAP.invalidate(cacheKey);
        }
    }

    public static boolean isCached(String cacheKey) {
        return !isCacheExpired(cacheKey);
    }

    /**
     * 获取缓存文件路径
     *
     * @param id
     * @param distPath
     * @return
     */
    public static String getCacheFilePath(Long id, String distPath) {
        String projectPath = System.getProperty("user.dir");
        String tmpDirPath = String.format("%s/.tmp/cache/%s", projectPath, id);
        return tmpDirPath + "/" + distPath;
    }

}
