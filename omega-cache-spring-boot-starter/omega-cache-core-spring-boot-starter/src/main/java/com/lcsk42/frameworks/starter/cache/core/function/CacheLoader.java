package com.lcsk42.frameworks.starter.cache.core.function;

/**
 * 表示当当前值缺失或过时时，提供要加载到缓存中的值的回调。
 *
 * <p>
 * 该函数式接口抽象了获取或计算值所需的逻辑，通常用于缓存未命中的场景中将新数据填充到缓存。
 * </p>
 *
 * @param <T> 要加载到缓存中的值的类型
 */
@FunctionalInterface
public interface CacheLoader<T> {

  /**
   * 加载并返回要缓存的值。
   *
   * @return 要缓存的值，除非缓存策略明确允许，否则不得为 {@code null}
   */
  T get();
}

