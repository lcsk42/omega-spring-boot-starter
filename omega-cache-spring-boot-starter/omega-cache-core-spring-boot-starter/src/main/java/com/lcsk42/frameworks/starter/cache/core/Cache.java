package com.lcsk42.frameworks.starter.cache.core;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Collection;

/**
 * 统一的缓存接口定义。
 * <p>
 * 该接口提供与底层缓存系统（如 Redis、Caffeine 等）交互的一致性 API。 支持基本的操作如获取、存入、删除和存在性检查。
 * </p>
 */
public interface Cache {

  /**
   * 通过键从缓存中获取对象。
   *
   * @param key 缓存键（不可为空字符串）
   * @param clazz 预期的对象类型
   * @param <T> 值的类型
   * @return 缓存的对象，若未找到则返回 {@code null}
   */
  <T> T get(@NotBlank String key, Class<T> clazz);

  /**
   * 将对象存入缓存。
   *
   * @param key 缓存键（不可为空字符串）
   * @param value 要存储的对象
   */
  void put(@NotBlank String key, Object value);

  /**
   * 仅当所有指定键都不存在时才存入键值对。 用于确保跨多个键的原子唯一性。
   *
   * @param keys 要检查的键集合（不可为 null）
   * @return {@code true} 如果所有键都不存在且存入操作成功， {@code false} 如果任一键已存在
   */
  Boolean putIfAllAbsent(@NotNull Collection<String> keys);

  /**
   * 通过键从缓存中删除对象。
   *
   * @param key 要删除的缓存键（不可为空字符串）
   * @return {@code true} 如果键被删除，{@code false} 否则
   */
  Boolean delete(@NotBlank String key);

  /**
   * 从缓存中删除多个键。
   *
   * @param keys 要删除的键集合（不可为 null）
   * @return 成功删除的键数量
   */
  Long delete(@NotNull Collection<String> keys);

  /**
   * 检查键是否存在于缓存中。
   *
   * @param key 要检查的键（不可为空字符串）
   * @return {@code true} 如果键存在，{@code false} 否则
   */
  Boolean hasKey(@NotBlank String key);

  /**
   * 获取原生缓存实现实例（如 RedisTemplate、CaffeineCache）。
   *
   * @return 底层缓存组件
   */
  Object getInstance();
}
