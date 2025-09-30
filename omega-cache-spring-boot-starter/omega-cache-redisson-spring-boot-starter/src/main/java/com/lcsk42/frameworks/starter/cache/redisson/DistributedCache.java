package com.lcsk42.frameworks.starter.cache.redisson;

import com.lcsk42.frameworks.starter.cache.core.Cache;
import com.lcsk42.frameworks.starter.cache.core.function.CacheGetFilter;
import com.lcsk42.frameworks.starter.cache.core.function.CacheGetIfAbsent;
import com.lcsk42.frameworks.starter.cache.core.function.CacheLoader;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.redisson.api.RBloomFilter;

import java.time.Duration;

public interface DistributedCache extends Cache {

    /**
     * 获取缓存值。若未找到，则使用提供的 {@link CacheLoader} 加载该值。
     */
    <T> T get(@NotBlank String key, Class<T> clazz, CacheLoader<T> cacheLoader, Duration timeout);

    /**
     * 安全获取缓存值。若未找到，则使用提供的 {@link CacheLoader} 加载该值。 有助于防止缓存击穿和雪崩问题。 适用于不对外暴露的内部接口。
     */
    <T> T safeGet(@NotBlank String key,
            Class<T> clazz,
            CacheLoader<T> cacheLoader,
            Duration timeout);

    /**
     * 使用布隆过滤器安全获取缓存值。若未找到，则使用 {@link CacheLoader} 加载该值。 有助于防止缓存穿透、击穿和雪崩。 适用于对外暴露的接口；需要客户端提供布隆过滤器。
     */
    <T> T safeGet(@NotBlank String key,
            Class<T> clazz,
            CacheLoader<T> cacheLoader,
            Duration timeout,
            RBloomFilter<String> bloomFilter);

    /**
     * 使用布隆过滤器和过滤器检查安全获取缓存值。 若未找到，则使用 {@link CacheLoader} 加载该值。 有助于防止缓存穿透、击穿和雪崩。 同时解决布隆过滤器无法删除条目的问题。
     * 适用于对外暴露的接口。
     */
    <T> T safeGet(@NotBlank String key,
            Class<T> clazz,
            CacheLoader<T> cacheLoader,
            Duration timeout,
            RBloomFilter<String> bloomFilter,
            CacheGetFilter<String> cacheCheckFilter);

    /**
     * 使用布隆过滤器、过滤器检查和回退处理器安全获取缓存值。 若未找到，则使用 {@link CacheLoader} 加载该值。 有助于防止缓存穿透、击穿和雪崩。 适用于对外暴露的接口。
     */
    <T> T safeGet(@NotBlank String key,
            Class<T> clazz,
            CacheLoader<T> cacheLoader,
            Duration timeout,
            RBloomFilter<String> bloomFilter,
            CacheGetFilter<String> cacheGetFilter,
            CacheGetIfAbsent<String> cacheGetIfAbsent);

    /**
     * 以自定义过期时间安全存入缓存值并将键添加到布隆过滤器。 大幅降低缓存穿透、击穿和雪崩的几率。 适用于对外暴露的接口。
     */
    void safePut(@NotBlank String key, Object value, Duration timeout,
            RBloomFilter<String> bloomFilter);

    /**
     * 统计指定键在缓存中存在的数量。
     */
    long countExistingKeys(@NotNull String... keys);
}
