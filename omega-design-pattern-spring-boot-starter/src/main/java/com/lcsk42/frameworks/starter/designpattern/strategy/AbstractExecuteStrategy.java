package com.lcsk42.frameworks.starter.designpattern.strategy;

/**
 * 策略模式接口 (采用函数式风格优化)
 *
 * @param <T> 输入类型 (请求参数)
 * @param <R> 输出类型 (响应结果)
 */
public interface AbstractExecuteStrategy<T, R> {

  /**
   * 获取策略的唯一标识符 (例如策略名称)
   *
   * @return 策略名称或唯一标识符
   */
  String getStrategyName();

  /**
   * 获取模式匹配键 (可选，用于动态路由)
   *
   * @return 匹配模式，默认返回 null 表示未启用此功能
   */
  default String getPatternMatchKey() {
    return null;
  }

  /**
   * 执行不返回值的策略 (类似 Consumer.accept)
   *
   * @param input 输入参数
   * @throws UnsupportedOperationException 如果策略未实现此方法
   */
  default void accept(T input) {
    throw new UnsupportedOperationException("accept() must be implemented");
  }

  /**
   * 执行策略并返回结果 (类似 Function.apply)
   *
   * @param input 输入参数
   * @return 策略执行结果
   * @throws UnsupportedOperationException 如果策略未实现此方法
   */
  default R apply(T input) {
    throw new UnsupportedOperationException("apply() must be implemented");
  }

  /**
   * 检查策略是否支持给定标识符
   *
   * @param strategyKey 策略标识符
   * @return true 如果策略支持给定的键，否则返回 false
   */
  default boolean supports(String strategyKey) {
    return getStrategyName().equals(strategyKey);
  }
}
