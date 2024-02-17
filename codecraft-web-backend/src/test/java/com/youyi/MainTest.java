package com.youyi;

import cn.hutool.cache.Cache;
import cn.hutool.cache.CacheUtil;
import cn.hutool.core.date.DateUnit;
import org.junit.jupiter.api.Test;

/**
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
public class MainTest {

    public static void main(String[] args) {
    }

    @Test
    public void lruCacheTest() {
        Cache<String, String> lruCache = CacheUtil.newLRUCache(3);
        lruCache.put("key1", "value1", DateUnit.SECOND.getMillis() * 3);
        lruCache.put("key2", "value2", DateUnit.SECOND.getMillis() * 3);
        lruCache.put("key3", "value3", DateUnit.SECOND.getMillis() * 3);
        // 使用时间推近
        lruCache.get("key1");
        lruCache.put("key4", "value4", DateUnit.SECOND.getMillis() * 3);

        String value1 = lruCache.get("key1");
        // 应该不为空
        System.out.println(value1);
        // 由于缓存容量只有3，当加入第四个元素的时候，根据LRU规则，最少使用的将被移除（2被移除）
        String value2 = lruCache.get("key2");
        // 应该为空
        System.out.println(value2);
    }
}
