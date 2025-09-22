package com.lcsk42.frameworks.starter.designpattern.builder;

import java.io.Serializable;

/**
 * 构建器模式抽象接口
 *
 * <p>
 * 该接口定义了用于创建复杂对象的构建器的契约。 构建器模式允许通过分步方式构造对象，提供了一种灵活配置各种参数来构建对象的方法。
 * </p>
 *
 * @param <T> 构建器将创建的对象类型
 */
public interface Builder<T> extends Serializable {
  /**
   * 构建方法
   *
   * <p>
   * 此方法将组装对象并返回完全构造的实例。 构建器在其生命周期中收集必要数据，并使用这些数据构建并返回最终的 {@code T} 类型对象。
   * </p>
   *
   * @return 构建完成的 {@code T} 类型对象
   */
  T build();
}
