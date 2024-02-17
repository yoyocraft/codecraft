package com.youyi.craft.manager;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 本地文件缓存管理器
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
public class LocalFileCacheManager {

    private static final Map<String, Long> CACHE_EXPIRATION_MAP = new ConcurrentHashMap<>(16);
    private static final Queue<String> CACHE_ACCESS_QUEUE = new LinkedList<>();


    /**
     * 缓存过期时间 1天
     */
    private static final long CACHE_EXPIRATION_TIME = 60 * 60 * 24;

    /**
     * 本地缓存最大上限 10个
     */
    private static final int CACHE_MAX_COUNT = 10;

    /**
     * 更新缓存过期时间
     *
     * @param cacheFilePath 缓存文件路径
     */
    public static void updateCacheExpiration(String cacheFilePath) {
        if (CACHE_EXPIRATION_MAP.size() >= CACHE_MAX_COUNT) {
            evictLRUCache(); // 缓存已满，淘汰最久未使用的缓存
        }
        CACHE_EXPIRATION_MAP.put(cacheFilePath, System.currentTimeMillis() + CACHE_EXPIRATION_TIME);
        CACHE_ACCESS_QUEUE.add(cacheFilePath); // 更新缓存访问顺序
    }

    private static void evictLRUCache() {
        List<String> expiredCacheFilePaths = getExpiredCacheFilePaths();
        // 有过期的缓存，先清理过期的
        if (CollUtil.isNotEmpty(expiredCacheFilePaths)) {
            // 清理过期文件
            expiredCacheFilePaths.forEach(filePath -> {
                FileUtil.del(filePath);
                CACHE_EXPIRATION_MAP.remove(filePath);
                CACHE_ACCESS_QUEUE.remove(filePath);
            });
            return;
        }
        // 没有过期的缓存，再清理醉酒没使用的
        String leastRecentlyUsedCacheFilePath = CACHE_ACCESS_QUEUE.poll(); // 获取最久未使用的缓存
        if (leastRecentlyUsedCacheFilePath != null) {
            FileUtil.del(leastRecentlyUsedCacheFilePath); // 删除最久未使用的缓存文件
            CACHE_EXPIRATION_MAP.remove(leastRecentlyUsedCacheFilePath); // 从缓存过期时间map中移除
        }
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

    private static String getCacheDirPath() {
        String projectPath = System.getProperty("user.dir");
        return String.format("%s/.tmp/cache", projectPath);
    }

    private static int getCacheFileCount() {
        return FileUtil.loopFiles(Paths.get(getCacheDirPath()), 1, null).size();
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
