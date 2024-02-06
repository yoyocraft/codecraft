package com.youyi.craft.manager;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 本地文件缓存管理器
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
public class LocalFileCacheManager {

    private static final Map<String, Long> CACHE_EXPIRATION_MAP = new ConcurrentHashMap<>(16);

    /**
     * 缓存过期时间 1周
     */
    private static final long CACHE_EXPIRATION_TIME = 60 * 60 * 24 * 7;

    /**
     * 更新缓存过期时间
     *
     * @param cacheFilePath 缓存文件路径
     */
    public static void updateCacheExpiration(String cacheFilePath) {
        CACHE_EXPIRATION_MAP.put(cacheFilePath, System.currentTimeMillis() + CACHE_EXPIRATION_TIME);
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

    /**
     * 判断缓存是否过期
     *
     * @param cacheFilePath 缓存文件路径
     * @return 是否过期
     */
    public static boolean isCacheExpired(String cacheFilePath) {
        Long expirationTime = CACHE_EXPIRATION_MAP.get(cacheFilePath);
        if (expirationTime == null) {
            // 缓存不存在或已经被清理，视为过期
            return true;
        }
        return expirationTime < System.currentTimeMillis();
    }

    /**
     * 获取过期缓存文件路径
     *
     * @return
     */
    public static List<String> getExpiredCacheFilePaths() {
        return CACHE_EXPIRATION_MAP.keySet().stream()
                .filter(LocalFileCacheManager::isCacheExpired)
                .collect(Collectors.toList());
    }

    /**
     * 清理缓存
     *
     * @param cacheKeyList
     */
    public static void clearCache(List<String> cacheKeyList) {
        if (CollUtil.isEmpty(cacheKeyList)) {
            return;
        }
        for (String cacheKey : cacheKeyList) {
            FileUtil.del(cacheKey);
            CACHE_EXPIRATION_MAP.remove(cacheKey);
        }
    }

    /**
     * 清理过期文件缓存
     */
    public static void clearExpireCache() {
        List<String> expiredCacheFilePaths = getExpiredCacheFilePaths();
        if (CollUtil.isEmpty(expiredCacheFilePaths)) {
            return;
        }
        clearCache(expiredCacheFilePaths);
    }

    /**
     * 判断缓存是否存在
     *
     * @param cacheKey
     * @return
     */
    public static boolean isCached(String cacheKey) {
        return CACHE_EXPIRATION_MAP.containsKey(cacheKey);
    }

    /**
     * 判断缓存是否存在
     *
     * @param id
     * @param distPath
     * @return
     */
    public static boolean isCached(Long id, String distPath) {
        String cacheFilePath = getCacheFilePath(id, distPath);
        return CACHE_EXPIRATION_MAP.containsKey(cacheFilePath);
    }

}
