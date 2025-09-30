package com.lcsk42.frameworks.starter.cache.redisson;

import com.lcsk42.frameworks.starter.cache.core.function.CacheGetFilter;
import com.lcsk42.frameworks.starter.cache.core.function.CacheGetIfAbsent;
import com.lcsk42.frameworks.starter.cache.core.function.CacheLoader;
import com.lcsk42.frameworks.starter.cache.core.util.CacheUtil;
import com.lcsk42.frameworks.starter.cache.redisson.config.RedisDistributedProperties;
import com.lcsk42.frameworks.starter.core.Singleton;
import com.lcsk42.frameworks.starter.json.jackson.util.JacksonUtil;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class StringRedisTemplateProxy implements DistributedCache {

    private final StringRedisTemplate stringRedisTemplate;
    private final RedisDistributedProperties redisProperties;
    private final RedissonClient redissonClient;

    private static final String LUA_PUT_IF_ABSENT_SCRIPT_PATH = "lua/putIfAbsent.lua";
    private static final String LUA_PUT_IF_EXISTS_SCRIPT_PATH = "lua/putIfExists.lua";
    private static final String LUA_PUT_IF_ALL_ABSENT_SCRIPT_PATH = "lua/putIfAllAbsent.lua";
    private static final String SAFE_GET_DISTRIBUTED_LOCK_KEY_PREFIX =
            "safe_get_distributed_lock_get:";


    @Override
    public Duration getDefaultTimeOut() {
        return redisProperties.getTimeout();
    }

    @Override
    public <T> T get(String key, Class<T> clazz, CacheLoader<T> cacheLoader, Duration timeout) {
        T result = get(key, clazz);
        if (!CacheUtil.isNullOrBlank(result)) {
            return result;
        }
        return loadAndSet(key, cacheLoader, timeout, false, null);
    }

    @Override
    public <T> T safeGet(String key, Class<T> clazz, CacheLoader<T> cacheLoader, Duration timeout) {
        return get(key, clazz, cacheLoader, timeout);
    }

    @Override
    public <T> T safeGet(String key, Class<T> clazz, CacheLoader<T> cacheLoader, Duration timeout,
            RBloomFilter<String> bloomFilter) {
        return safeGet(key, clazz, cacheLoader, timeout, bloomFilter, null, null);
    }

    @Override
    public <T> T safeGet(String key, Class<T> clazz, CacheLoader<T> cacheLoader, Duration timeout,
            RBloomFilter<String> bloomFilter, CacheGetFilter<String> cacheCheckFilter) {
        return safeGet(key, clazz, cacheLoader, timeout, bloomFilter, cacheCheckFilter, null);
    }

    @Override
    public <T> T safeGet(String key, Class<T> clazz,
            CacheLoader<T> cacheLoader,
            Duration timeout,
            RBloomFilter<String> bloomFilter,
            CacheGetFilter<String> cacheGetFilter,
            CacheGetIfAbsent<String> cacheGetIfAbsent) {
        T result = get(key, clazz);
        // 如果缓存结果不为 null 或不为空，则返回缓存结果
        // 使用函数来决定是否返回 null 以支持不可删除的 Bloom 过滤器场景
        // 如果两次检查都失败，当 Bloom 过滤器不包含该键时返回 null
        if (!CacheUtil.isNullOrBlank(result)
                || Optional.ofNullable(cacheGetFilter).map(each -> each.filter(key)).orElse(false)
                || Optional.ofNullable(bloomFilter).map(each -> !each.contains(key))
                        .orElse(false)) {
            return result;
        }
        RLock lock = redissonClient.getLock(SAFE_GET_DISTRIBUTED_LOCK_KEY_PREFIX + key);
        lock.lock();
        try {
            if (CacheUtil.isNullOrBlank(result = get(key, clazz))) {
                if (CacheUtil
                        .isNullOrBlank(result =
                                loadAndSet(key, cacheLoader, timeout, true, bloomFilter))) {
                    Optional.ofNullable(cacheGetIfAbsent).ifPresent(each -> each.accept(key));
                }
            }
        } finally {
            lock.unlock();
        }
        return result;
    }

    @Override
    public void safePut(String key, Object value, Duration timeout,
            RBloomFilter<String> bloomFilter) {
        put(key, value, timeout);
        if (bloomFilter != null) {
            bloomFilter.add(key);
        }
    }

    @Override
    public long countExistingKeys(@NotNull String... keys) {
        return Optional.ofNullable(stringRedisTemplate.countExistingKeys(List.of(keys)))
                .orElse(NumberUtils.LONG_ZERO);
    }

    @Override
    public <T> T get(String key, Class<T> clazz) {
        String value = stringRedisTemplate.opsForValue().get(key);
        return handleResult(value, clazz);
    }

    @Override
    public <T> void put(String key, T value, Duration timeout) {
        convertValue(value)
                .ifPresent(val -> stringRedisTemplate.opsForValue().set(key, val,
                        convertTimeout(timeout), TimeUnit.NANOSECONDS));
    }

    @Override
    public <T> boolean putIfAbsent(String key, T value, Duration timeout) {
        DefaultRedisScript<Boolean> script = getDefaultRedisScript(LUA_PUT_IF_ABSENT_SCRIPT_PATH);

        if (Objects.isNull(script)) {
            return false;
        }

        return convertValue(value)
                .map(val -> stringRedisTemplate.execute(
                        script,
                        Collections.singletonList(key),
                        val,
                        String.valueOf(convertTimeout(timeout))))
                .map(BooleanUtils::isTrue)
                .orElse(false);
    }

    @Override
    public <T> boolean putIfExists(String key, T value, Duration timeout) {
        DefaultRedisScript<Boolean> script = getDefaultRedisScript(LUA_PUT_IF_EXISTS_SCRIPT_PATH);

        if (Objects.isNull(script)) {
            return false;
        }

        return convertValue(value)
                .map(val -> stringRedisTemplate.execute(script,
                        Collections.singletonList(key),
                        val,
                        String.valueOf(convertTimeout(timeout))))
                .map(BooleanUtils::isTrue)
                .orElse(false);

    }

    @Override
    public boolean putIfAllAbsent(Collection<String> keys) {

        DefaultRedisScript<Boolean> script =
                getDefaultRedisScript(LUA_PUT_IF_ALL_ABSENT_SCRIPT_PATH);

        if (Objects.isNull(script)) {
            return false;
        }
        Boolean result = stringRedisTemplate.execute(script,
                List.copyOf(keys),
                redisProperties.getTimeout().toNanos(),
                TimeUnit.NANOSECONDS);
        return BooleanUtils.isTrue(result);
    }

    @Override
    public boolean delete(String key) {
        return BooleanUtils.isTrue(stringRedisTemplate.delete(key));
    }

    @Override
    public long delete(Collection<String> keys) {
        return Optional.ofNullable(stringRedisTemplate.delete(keys))
                .orElse(NumberUtils.LONG_ZERO);
    }

    @Override
    public long delete(String... keys) {
        return Optional.ofNullable(stringRedisTemplate.delete(List.of(keys)))
                .orElse(NumberUtils.LONG_ZERO);
    }


    @Override
    public long incr(String key) {
        return Optional.ofNullable(stringRedisTemplate.opsForValue().increment(key))
                .orElse(NumberUtils.LONG_ZERO);
    }

    @Override
    public long decr(String key) {
        return Optional.ofNullable(stringRedisTemplate.opsForValue().decrement(key))
                .orElse(NumberUtils.LONG_ZERO);
    }

    @Override
    public boolean expire(String key, Duration timeout) {
        return BooleanUtils
                .isTrue(stringRedisTemplate.expire(key, timeout.toNanos(), TimeUnit.NANOSECONDS));
    }

    @Override
    public long ttl(String key) {
        return Optional.ofNullable(stringRedisTemplate.getExpire(key))
                .orElse(NumberUtils.LONG_ZERO);
    }

    @Override
    public long ttl(String key, TimeUnit timeUnit) {
        return Optional.ofNullable(stringRedisTemplate.getExpire(key, timeUnit))
                .orElse(NumberUtils.LONG_ZERO);
    }

    @Override
    public boolean exists(String key) {
        return BooleanUtils.isTrue(stringRedisTemplate.hasKey(key));
    }

    @Override
    public <T> void hSet(String key, String field, T value) {
        convertValue(value).ifPresent(val -> stringRedisTemplate.opsForHash().put(key, field, val));
    }

    @Override
    public <T> T hGet(String key, String field, Class<T> clazz) {
        Object value = stringRedisTemplate.opsForHash().get(key, field);
        return handleResult(value, clazz);
    }

    @Override
    public <T> Map<String, T> hGet(String key, Class<T> clazz) {
        Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(key);
        Map<String, T> result = new HashMap<>();
        entries.forEach((k, v) -> result.put(k.toString(), handleResult(v, clazz)));
        return result;
    }

    @Override
    public boolean hExists(String key, String field) {
        return stringRedisTemplate.opsForHash().hasKey(key, field);
    }

    @Override
    public long hDelete(String key, @NotBlank Object... fields) {
        return stringRedisTemplate.opsForHash().delete(key, fields);
    }

    @Override
    public <T> boolean zAdd(String key, T value, double score) {
        return convertValue(value)
                .map(val -> stringRedisTemplate.opsForZSet().add(key, val, score))
                .map(BooleanUtils::isTrue)
                .orElse(false);
    }

    @Override
    public <T> double zScore(String key, T value) {
        return convertValue(value)
                .map(val -> stringRedisTemplate.opsForZSet().score(key, val))
                .orElse(NumberUtils.DOUBLE_ZERO);

    }

    @Override
    public <T> long zRank(String key, T value) {
        return convertValue(value)
                .map(val -> stringRedisTemplate.opsForZSet().rank(key, val))
                .orElse(NumberUtils.LONG_ZERO);
    }

    @Override
    public long zSize(String key) {
        return Optional.ofNullable(stringRedisTemplate.opsForZSet().size(key))
                .orElse(NumberUtils.LONG_ZERO);
    }

    @Override
    public <T> long zRemove(String key, T value) {
        return convertValue(value)
                .map(val -> stringRedisTemplate.opsForZSet().remove(key, val))
                .orElse(NumberUtils.LONG_ZERO);
    }

    @Override
    public long zRemoveRangeByScore(String key, double min, double max) {
        return Optional
                .ofNullable(stringRedisTemplate.opsForZSet().removeRangeByScore(key, min, max))
                .orElse(NumberUtils.LONG_ZERO);
    }

    @Override
    public long zRemoveRangeByRank(String key, int startIndex, int endIndex) {
        return Optional
                .ofNullable(stringRedisTemplate.opsForZSet().removeRange(key, startIndex, endIndex))
                .orElse(NumberUtils.LONG_ZERO);
    }

    @Override
    public <T> Collection<T> zRangeByScore(String key, double min, double max, Class<T> clazz) {
        Set<String> values = stringRedisTemplate.opsForZSet().rangeByScore(key, min, max);
        return values != null
                ? values.stream().map(value -> handleResult(value, clazz))
                        .collect(Collectors.toSet())
                : Set.of();

    }

    @Override
    public <T> Collection<T> zRangeByScore(String key, double min, double max, int offset,
            int count, Class<T> clazz) {
        Set<String> values =
                stringRedisTemplate.opsForZSet().rangeByScore(key, min, max, offset, count);
        return values != null
                ? values.stream().map(value -> handleResult(value, clazz))
                        .collect(Collectors.toSet())
                : Set.of();
    }

    @Override
    public long zCountRangeByScore(String key, double min, double max) {
        return Optional.ofNullable(stringRedisTemplate.opsForZSet().count(key, min, max))
                .orElse(NumberUtils.LONG_ZERO);
    }

    @Override
    public <T> double zSum(String key, Collection<T> values) {
        return values.stream()
                .mapToDouble(value -> zScore(key, value))
                .filter(Objects::nonNull)
                .sum();
    }

    @Override
    public <T> void sAdd(String key, T value) {
        convertValue(value).ifPresent(val -> stringRedisTemplate.opsForSet().add(key, val));
    }

    @Override
    public <T> long sAdd(String key, Collection<T> values) {
        String[] handleValues = values.stream()
                .map(this::convertValue)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toArray(String[]::new);
        if (handleValues.length > 0) {
            return Optional.ofNullable(stringRedisTemplate.opsForSet().add(key, handleValues))
                    .orElse(NumberUtils.LONG_ZERO);
        }
        return NumberUtils.LONG_ZERO;
    }

    @Override
    public <T> long sRemove(String key, T value) {
        return convertValue(value)
                .map(val -> stringRedisTemplate.opsForSet().remove(key, val))
                .orElse(NumberUtils.LONG_ZERO);
    }

    @Override
    public <T> long sRemove(String key, Collection<T> values) {
        Object[] handleValues = values.stream()
                .map(this::convertValue)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toArray(Object[]::new);

        if (handleValues.length > 0) {
            return Optional.ofNullable(stringRedisTemplate.opsForSet().remove(key, handleValues))
                    .orElse(NumberUtils.LONG_ZERO);
        }
        return NumberUtils.LONG_ZERO;
    }

    @Override
    public <T> Collection<T> sMembers(String key, Class<T> clazz) {
        return Optional.ofNullable(stringRedisTemplate.opsForSet().members(key))
                .orElse(Set.of())
                .stream()
                .map(val -> handleResult(val, clazz))
                .collect(Collectors.toSet());
    }

    @Override
    public <T> boolean sIsMember(String key, T value) {
        return convertValue(value)
                .map(val -> stringRedisTemplate.opsForSet().isMember(key, val))
                .map(BooleanUtils::isTrue)
                .orElse(false);
    }

    @Override
    public long sSize(String key) {
        return Optional.ofNullable(stringRedisTemplate.opsForSet().size(key))
                .orElse(NumberUtils.LONG_ZERO);
    }

    @Override
    public Object getInstance() {
        return stringRedisTemplate;
    }

    private DefaultRedisScript<Boolean> getDefaultRedisScript(String path) {
        return Singleton.get(path, () -> {
            DefaultRedisScript<Boolean> redisScript = new DefaultRedisScript<>();
            redisScript.setScriptSource(
                    new ResourceScriptSource(
                            new ClassPathResource(path)));
            redisScript.setResultType(Boolean.class);
            return redisScript;
        });
    }

    private <T> Optional<String> convertValue(T value) {
        if (value instanceof String val) {
            return Optional.of(val);
        } else {
            return Optional.ofNullable(JacksonUtil.toJSON(value));
        }
    }

    private long convertTimeout(Duration timeout) {
        return timeout != null ? timeout.toNanos() : -1;
    }

    private <T> T handleResult(Object value, Class<T> clazz) {
        if (Objects.isNull(value)) {
            return null;
        }
        if (String.class.isAssignableFrom(clazz)) {
            return clazz.cast(value);
        }
        return JacksonUtil.toBean(value.toString(), clazz);
    }

    private <T> T loadAndSet(String key,
            CacheLoader<T> cacheLoader,
            Duration timeout,
            boolean safeFlag,
            RBloomFilter<String> bloomFilter) {
        T result = cacheLoader.get();
        if (CacheUtil.isNullOrBlank(result)) {
            return result;
        }
        if (safeFlag) {
            safePut(key, result, timeout, bloomFilter);
        } else {
            put(key, result, timeout);
        }
        return result;
    }
}
