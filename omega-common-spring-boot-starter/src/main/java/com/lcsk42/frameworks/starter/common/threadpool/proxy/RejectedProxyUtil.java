package com.lcsk42.frameworks.starter.common.threadpool.proxy;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.lang.reflect.Proxy;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 用于创建线程池拒绝处理器代理包装器的工具类。 通过动态代理提供增强的拒绝策略功能。
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RejectedProxyUtil {

  /**
   * 创建拒绝策略处理器的代理包装器。 增强处理器的能力，例如拒绝计数等功能。
   *
   * @param rejectedExecutionHandler 实际的拒绝策略实现
   * @param rejectedNum 用于跟踪拒绝次数的计数器
   * @return 代理包装后的拒绝策略处理器
   */
  public static RejectedExecutionHandler createProxy(
      RejectedExecutionHandler rejectedExecutionHandler, AtomicLong rejectedNum) {
    // 使用动态代理模式实现：
    // 1. 增强线程池拒绝策略行为
    // 2. 添加拒绝告警等能力
    // 3. 通过延迟队列启用重试机制
    return (RejectedExecutionHandler) Proxy.newProxyInstance(
        rejectedExecutionHandler.getClass().getClassLoader(),
        new Class[] {RejectedExecutionHandler.class},
        new RejectedProxyInvocationHandler(rejectedExecutionHandler, rejectedNum));
  }
}
