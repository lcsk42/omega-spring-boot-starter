package com.lcsk42.frameworks.starter.cache.core;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Duration;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 统一的缓存接口定义。
 * <p>
 * 该接口提供与底层缓存系统（如 Redis、Caffeine 等）交互的一致性 API。 支持基本的操作如获取、存入、删除和存在性检查。
 * </p>
 */
public interface Cache {

    /**
     * 获取默认的超时时间
     *
     * @return 超时时间
     */
    Duration getDefaultTimeOut();

    /**
     * 通过键从缓存中获取对象。
     *
     * @param key 缓存键（不可为空字符串）
     * @return 缓存的对象，若未找到则返回 {@code null}
     */
    default String get(@NotBlank String key) {
        return get(key, String.class);
    }

    /**
     * 通过键从缓存中获取对象。
     *
     * @param key 缓存键（不可为空字符串）
     * @param clazz 预期的对象类型
     * @param <T> 值的类型
     * @return 缓存的对象，若未找到则返回 {@code null}
     */
    <T> T get(@NotBlank String key, @NotBlank Class<T> clazz);

    /**
     * 设置缓存
     *
     * @param key 缓存键（不可为空字符串）
     * @param value 要存储的对象
     */
    default <T> void put(@NotBlank String key, T value) {
        putIfAbsent(key, value, getDefaultTimeOut());
    }

    /**
     * 将值存入缓存并设置过期时间
     *
     * @param key 缓存键（不可为空字符串）
     * @param value 要存储的对象
     * @param timeout 过期时间
     */
    <T> void put(@NotBlank String key, T value, @NotNull Duration timeout);

    /**
     * 设置缓存
     *
     * <p>
     * 如果键已存在，则不设置
     * </p>
     *
     * @param key 键
     * @param value 值
     * @return true：设置成功；false：设置失败
     */
    default <T> boolean putIfAbsent(@NotBlank String key, T value) {
        return putIfAbsent(key, value, getDefaultTimeOut());
    }

    /**
     * 设置缓存
     *
     * <p>
     * 如果键已存在，则不设置
     * </p>
     *
     * @param key 键
     * @param value 值
     * @param timeout 过期时间
     * @return true：设置成功；false：设置失败
     */
    <T> boolean putIfAbsent(@NotBlank String key, T value, @NotNull Duration timeout);

    /**
     * 设置缓存
     * <p>
     * 如果键不存在，则不设置
     * </p>
     *
     * @param key 键
     * @param value 值
     * @return true：设置成功；false：设置失败
     */
    default <T> boolean putIfExists(@NotBlank String key, T value) {
        return putIfExists(key, value, getDefaultTimeOut());
    }

    /**
     * 设置缓存
     * <p>
     * 如果键不存在，则不设置
     * </p>
     *
     * @param key 键
     * @param value 值
     * @param timeout 过期时间
     * @return true：设置成功；false：设置失败
     */
    <T> boolean putIfExists(@NotBlank String key, T value, @NotNull Duration timeout);

    /**
     * 仅当所有指定键都不存在时才存入键值对。 用于确保跨多个键的原子唯一性。
     *
     * @param keys 要检查的键集合（不可为 null）
     * @return {@code true} 如果所有键都不存在且存入操作成功， {@code false} 如果任一键已存在
     */
    boolean putIfAllAbsent(@NotNull Collection<String> keys);

    /**
     * 通过键从缓存中删除对象。
     *
     * @param key 要删除的缓存键（不可为空字符串）
     * @return {@code true} 如果键被删除，{@code false} 否则
     */
    boolean delete(@NotBlank String key);

    /**
     * 从缓存中删除多个键。
     *
     * @param keys 要删除的键集合（不可为 null）
     * @return 成功删除的键数量
     */
    long delete(@NotNull Collection<String> keys);

    /**
     * 从缓存中删除多个键。
     *
     * @param keys 要删除的键集合（不可为 null）
     * @return 成功删除的键数量
     */
    long delete(@NotNull String... keys);

    /**
     * 递增 1
     *
     * @param key 键
     * @return 当前值
     */
    long incr(@NotBlank String key);

    /**
     * 递减 1
     *
     * @param key 键
     * @return 当前值
     */
    long decr(@NotBlank String key);

    /**
     * 设置缓存过期时间
     *
     * @param key 键
     * @param timeout 过期时间
     * @return true：设置成功；false：设置失败
     */
    boolean expire(@NotBlank String key, @NotNull Duration timeout);

    /**
     * 查询缓存剩余过期时间
     *
     * @param key 键
     * @return 缓存剩余过期时间（单位：毫秒）
     */
    long ttl(@NotBlank String key);

    /**
     * 查询缓存剩余过期时间
     *
     * @param key 键
     * @param timeUnit 时间单位
     * @return 缓存剩余过期时间
     */
    long ttl(@NotBlank String key, @NotNull TimeUnit timeUnit);

    /**
     * 检查键是否存在于缓存中。
     *
     * @param key 要检查的键（不可为空字符串）
     * @return {@code true} 如果键存在，{@code false} 否则
     */
    boolean exists(@NotBlank String key);

    /**
     * 设置 Hash 中指定字段的值
     *
     * @param key Hash 键
     * @param field 字段
     * @param value 值
     */
    <T> void hSet(@NotBlank String key, @NotBlank String field, T value);

    /**
     * 获取 Hash 中指定字段的值
     *
     * @param key Hash 键
     * @param field 字段
     * @return 值
     */
    default String hGet(@NotBlank String key, @NotBlank String field) {
        return hGet(key, field, String.class);
    }

    /**
     * 获取 Hash 中指定字段的值
     *
     * @param key Hash 键
     * @param field 字段
     * @param clazz 预期的对象类型
     * @param <T> 值的类型
     * @return 值
     */
    <T> T hGet(@NotBlank String key, @NotBlank String field, @NotNull Class<T> clazz);

    /**
     * 获取整个 Hash 的所有字段值
     *
     * @param key Hash 键
     * @return Map
     */
    default Map<String, String> hGet(@NotBlank String key) {
        return hGet(key, String.class);
    }

    /**
     * 获取整个 Hash 的所有字段值
     *
     * @param key Hash 键
     * @param clazz 预期的对象类型
     * @param <T> 值的类型
     * @return Map
     */
    <T> Map<String, T> hGet(@NotBlank String key, @NotNull Class<T> clazz);

    /**
     * 判断 Hash 中是否存在指定字段
     *
     * @param key Hash 键
     * @param field 字段
     * @return true：存在；false：不存在
     */
    boolean hExists(@NotBlank String key, @NotBlank String field);

    /**
     * 删除 Hash 中指定字段
     *
     * @param key Hash 键
     * @param fields 字段数组
     * @return 删除成功的字段数量
     */
    long hDelete(@NotBlank String key, @NotNull Object... fields);

    /**
     * 添加元素到 ZSet 中
     *
     * @param key 键
     * @param value 值
     * @param score 分数
     * @return true：添加成功；false：添加失败
     */
    <T> boolean zAdd(@NotBlank String key, T value, double score);

    /**
     * 查询 ZSet 中指定元素的分数
     *
     * @param key 键
     * @param value 值
     * @return 分数（null 表示元素不存在）
     */
    <T> double zScore(@NotBlank String key, T value);

    /**
     * 查询 ZSet 中指定元素的排名
     *
     * @param key 键
     * @param value 值
     * @return 排名（从 0 开始，null 表示元素不存在）
     */
    <T> long zRank(@NotBlank String key, T value);

    /**
     * 查询 ZSet 中的元素个数
     *
     * @param key 键
     * @return 元素个数
     */
    long zSize(@NotBlank String key);

    /**
     * 从 ZSet 中删除指定元素
     *
     * @param key 键
     * @param value 值
     * @return true：删除成功；false：删除失败
     */
    <T> long zRemove(@NotBlank String key, T value);

    /**
     * 删除 ZSet 中指定分数范围内的元素
     *
     * @param key 键
     * @param min 最小分数（包含）
     * @param max 最大分数（包含）
     * @return 删除的元素个数
     */
    long zRemoveRangeByScore(@NotBlank String key, double min, double max);

    /**
     * 删除 ZSet 中指定排名范围内的元素
     *
     * <p>
     * 索引从 0 开始。<code>-1<code> 表示最高分，<code>-2<code> 表示第二高分。
     * </p>
     *
     * @param key 键
     * @param startIndex 起始索引
     * @param endIndex 结束索引
     * @return 删除的元素个数
     */
    long zRemoveRangeByRank(@NotBlank String key, int startIndex, int endIndex);

    /**
     * 根据分数范围查询 ZSet 中的元素列表
     *
     * @param key 键
     * @param min 最小分数（包含）
     * @param max 最大分数（包含）
     * @return 元素列表
     */
    default Collection<String> zRangeByScore(@NotBlank String key, double min, double max) {
        return zRangeByScore(key, min, max, String.class);
    }

    /**
     * 根据分数范围查询 ZSet 中的元素列表
     *
     * @param key 键
     * @param min 最小分数（包含）
     * @param max 最大分数（包含）
     * @param clazz 预期的对象类型
     * @param <T> 值的类型
     * @return 元素列表
     */
    <T> Collection<T> zRangeByScore(@NotBlank String key, double min, double max,
            @NotNull Class<T> clazz);

    /**
     * 根据分数范围查询 ZSet 中的元素列表
     *
     * @param key 键
     * @param min 最小分数（包含）
     * @param max 最大分数（包含）
     * @param offset 偏移量
     * @param count 数量
     * @return 元素列表
     */
    default Collection<String> zRangeByScore(@NotBlank String key, double min, double max,
            int offset, int count) {
        return zRangeByScore(key, min, max, offset, count, String.class);
    }

    /**
     * 根据分数范围查询 ZSet 中的元素列表
     *
     * @param key 键
     * @param min 最小分数（包含）
     * @param max 最大分数（包含）
     * @param offset 偏移量
     * @param count 数量
     * @param clazz 预期的对象类型
     * @param <T> 值的类型
     * @return 元素列表
     */
    <T> Collection<T> zRangeByScore(@NotBlank String key, double min, double max, int offset,
            int count, @NotNull Class<T> clazz);

    /**
     * 根据分数范围查询 ZSet 中的元素个数
     *
     * @param key 键
     * @param min 最小分数（包含）
     * @param max 最大分数（包含）
     * @return 元素个数
     */
    long zCountRangeByScore(@NotBlank String key, double min, double max);

    /**
     * 计算 ZSet 中多个元素的分数之和
     *
     * @param key 键
     * @param values 值列表
     * @return 分数之和
     */
    <T> double zSum(@NotBlank String key, Collection<T> values);

    /**
     * 向集合添加元素
     *
     * @param key 集合键
     * @param value 要添加的值
     */
    <T> void sAdd(@NotBlank String key, T value);

    /**
     * 批量向集合添加元素
     *
     * @param key 集合键
     * @param values 要添加的值集合
     * @return 成功添加的元素数量
     */
    <T> long sAdd(@NotBlank String key, Collection<T> values);

    /**
     * 从集合中移除元素
     *
     * @param key 集合键
     * @param value 要移除的值
     * @return 是否成功移除
     */
    <T> long sRemove(@NotBlank String key, T value);

    /**
     * 批量从集合中移除元素
     *
     * @param key 集合键
     * @param values 要移除的值集合
     * @return 成功移除的元素数量
     */
    <T> long sRemove(@NotBlank String key, @NotNull Collection<T> values);

    /**
     * 获取集合中的所有元素
     *
     * @param key 集合键
     * @return 元素集合
     */
    default Collection<String> sMembers(@NotBlank String key) {
        return sMembers(key, String.class);
    }

    /**
     * 获取集合中的所有元素
     *
     * @param key 集合键
     * @return 元素集合
     */
    <T> Collection<T> sMembers(@NotBlank String key, @NotNull Class<T> clazz);

    /**
     * 判断元素是否在集合中
     *
     * @param key 集合键
     * @param value 要检查的值
     * @return 是否存在
     */
    <T> boolean sIsMember(@NotBlank String key, T value);

    /**
     * 获取集合大小
     *
     * @param key 集合键
     * @return 集合元素数量
     */
    long sSize(@NotBlank String key);



    /**
     * 获取原生缓存实现实例（如 RedisTemplate、CaffeineCache）。
     *
     * @return 底层缓存组件
     */
    Object getInstance();
}
