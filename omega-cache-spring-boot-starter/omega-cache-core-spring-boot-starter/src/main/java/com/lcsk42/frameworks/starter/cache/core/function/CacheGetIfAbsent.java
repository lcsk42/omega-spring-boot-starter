package com.lcsk42.frameworks.starter.cache.core.function;

/**
 * 当缓存查找返回空值或被视为无效时执行的回调。
 *
 * <p>
 * 该函数式接口通常用于定义当缓存值缺失、过期或被过滤时要执行的自定义操作。 它支持延迟操作，如日志记录、统计或触发数据加载。
 * </p>
 *
 * @param <T> 传递给回调的参数类型
 */
@FunctionalInterface
public interface CacheGetIfAbsent<T> {

  /**
   * 当缓存缺失或值无效时执行自定义逻辑。
   *
   * @param param 可选参数，可用于辅助后备逻辑
   */
  void accept(T param);
}

