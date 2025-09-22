package com.lcsk42.frameworks.starter.cache.core.function;

/**
 * 缓存检索过程中的过滤策略。
 *
 * <p>
 * 该函数式接口用于确定在缓存读取操作期间是否应接受或拒绝缓存值。 在缓存数据可能过时或无效且需要在使用前进行验证的场景中尤为有用。
 * </p>
 *
 * @param <T> 待过滤对象的类型
 */
@FunctionalInterface
public interface CacheGetFilter<T> {

  /**
   * 判断给定值是否应被过滤（即视为无效）。
   *
   * @param param 待验证的对象
   * @return {@code true} 如果该值应被过滤（拒绝），{@code false} 如果有效且可使用
   */
  boolean filter(T param);
}

