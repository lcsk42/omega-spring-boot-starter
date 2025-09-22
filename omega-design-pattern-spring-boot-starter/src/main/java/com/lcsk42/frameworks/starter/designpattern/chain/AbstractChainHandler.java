package com.lcsk42.frameworks.starter.designpattern.chain;

import org.springframework.core.Ordered;

/**
 * 抽象责任链处理器接口
 *
 * <p>
 * 该接口定义了责任链模式中处理器的契约。 处理器负责处理请求并在需要时将其传递给责任链中的下一个处理器。 责任链中的每个处理器可以处理请求或将其转发给下一个处理器。
 * </p>
 *
 * @param <T> 处理器将要处理的请求参数类型
 */
public interface AbstractChainHandler<T> extends Ordered {

  /**
   * 执行责任链逻辑
   *
   * <p>
   * 此方法用于处理请求。责任链中的每个处理器要么处理请求， 要么将其传递给责任链中的下一个处理器。
   * </p>
   *
   * @param input 责任链执行的输入数据，通常包含待处理的数据
   */
  void accept(T input);

  /**
   * 获取责任链组件标识符
   *
   * @return 责任链组件的标识符，通常用于区分责任链中的不同处理器
   */
  String getHandlerName();
}
