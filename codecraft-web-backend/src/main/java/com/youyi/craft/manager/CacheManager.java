package com.youyi.craft.manager;

import cn.hutool.core.util.StrUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import javax.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
@Component
public class CacheManager {


    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    private final Cache<String, Object> localCache = Caffeine.newBuilder()
            .expireAfterWrite(100, TimeUnit.MINUTES)
            .maximumSize(10_000)
            .build();

    /**
     * 写缓存
     *
     * @param key
     * @param value
     */
    public void put(String key, Object value) {
        localCache.put(key, value);
        redisTemplate.opsForValue().set(key, value, 100, TimeUnit.MINUTES);
    }

    /**
     * 读缓存
     *
     * @param key
     * @return
     */
    public Object get(String key) {
        // 尝试从本地缓存中获取
        Object value = localCache.getIfPresent(key);
        if (Objects.nonNull(value)) {
            return value;
        }

        // 本地缓存未命中，从 Redis 中获取
        value = redisTemplate.opsForValue().get(key);
        if (Objects.nonNull(value)) {
            // 将 Redis 中的值写入本地缓存
            localCache.put(key, value);
        }
        return value;
    }

    /**
     * 删除缓存
     *
     * @param key
     */
    public void delete(String key) {
        localCache.invalidate(key);
        redisTemplate.delete(key);
    }


}
