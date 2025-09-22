package com.lcsk42.frameworks.starter.common.threadpool.proxy;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 线程池拒绝策略执行的代理处理器。 跟踪拒绝次数并在拒绝发生时记录错误日志。
 */
@Slf4j
@AllArgsConstructor
public class RejectedProxyInvocationHandler implements InvocationHandler {

  /**
   * 被代理的实际拒绝策略实现
   */
  private final Object target;

  /**
   * 用于跟踪拒绝次数的计数器
   */
  private final AtomicLong rejectCount;

  /**
   * 调用拒绝策略方法，同时跟踪拒绝次数并记录错误日志
   *
   * @param proxy 被调用方法的代理实例
   * @param method 对应于接口方法的 Method 实例
   * @param args 包含方法参数的对象数组
   * @return 方法调用的结果
   * @throws Throwable 如果底层方法抛出异常
   */
  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    // 增加拒绝计数器
    rejectCount.incrementAndGet();

    try {
      // 记录线程池拒绝的错误日志
      log.error(
          "The thread pool executes the rejection strategy, and the alarm is simulated here...");

      // 调用实际的拒绝策略方法
      return method.invoke(target, args);
    } catch (InvocationTargetException ex) {
      // 解包底层异常
      throw ex.getCause();
    }
  }
}
