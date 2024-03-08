package com.youyi.craft.manager;

import cn.hutool.core.codec.Base64Encoder;
import cn.hutool.json.JSONUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.youyi.craft.constant.GeneratorConstant;
import com.youyi.craft.model.dto.generator.GeneratorQueryRequest;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import javax.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * 缓存管理
 * <p>
 * 缓存一致性问题讨论：缓存的是首页数据，首页数据更新不频繁，因为代码生成器的更新是需要校验和审核的，因此这部分的缓存可以不考虑一致性问题。通过过期时间兜底
 *
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

    /**
     * 获得缓存key
     *
     * @param generatorQueryRequest
     * @return
     */
    public static String getPageCacheKey(GeneratorQueryRequest generatorQueryRequest) {
        String jsonStr = JSONUtil.toJsonStr(generatorQueryRequest);
        // 请求参数编码
        String base64 = Base64Encoder.encode(jsonStr);
        return GeneratorConstant.CACHE_KEY_PREFIX + base64;
    }

    public static String getIndexPageCacheKey() {
        GeneratorQueryRequest defaultReq = new GeneratorQueryRequest();
        defaultReq.setCurrent(1);
        defaultReq.setPageSize(12);
        defaultReq.setSortField("updateTime");
        defaultReq.setSortOrder("descend");
        return CacheManager.getPageCacheKey(defaultReq);
    }

}
