package com.lcsk42.frameworks.starter.common.threadpool.build;

import com.lcsk42.frameworks.starter.designpattern.builder.Builder;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.io.Serial;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 线程工厂构建器，提供灵活的线程配置选项。
 * <p>
 * 支持设置线程名称前缀、守护状态、优先级和未捕获异常处理器。 使用构建器模式实现方法链式调用。
 * </p>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ThreadFactoryBuilder implements Builder<ThreadFactory> {

  @Serial
  private static final long serialVersionUID = 1L;

  // 基础线程工厂（默认为 Executors.defaultThreadFactory）
  private ThreadFactory backingThreadFactory;
  // 线程名称前缀（格式："前缀_计数器"）
  private String namePrefix;
  // 是否为守护线程
  private Boolean daemon;
  // 线程优先级（1-10）
  private Integer priority;
  // 未捕获异常处理器
  private Thread.UncaughtExceptionHandler uncaughtExceptionHandler;

  /**
   * 创建新的 ThreadFactoryBuilder 实例
   *
   * @return 新的 ThreadFactoryBuilder 实例
   */
  public static ThreadFactoryBuilder builder() {
    return new ThreadFactoryBuilder();
  }

  /**
   * 设置基础线程工厂。如未设置，则使用 Executors.defaultThreadFactory()
   *
   * @param backingThreadFactory 基础线程工厂
   * @return 当前构建器实例
   * @throws NullPointerException 如果 backingThreadFactory 为 null
   */
  public ThreadFactoryBuilder threadFactory(@NonNull ThreadFactory backingThreadFactory) {
    this.backingThreadFactory =
        Objects.requireNonNull(backingThreadFactory, "backingThreadFactory cannot be null");
    return this;
  }

  /**
   * 设置线程名称前缀。设置后线程名称将采用"前缀_计数器"的格式
   *
   * @param namePrefix 线程名称前缀
   * @return 当前构建器实例
   */
  public ThreadFactoryBuilder prefix(String namePrefix) {
    this.namePrefix = namePrefix;
    return this;
  }

  /**
   * 设置线程是否为守护线程
   *
   * @param daemon true 表示守护线程，false 反之
   * @return 当前构建器实例
   */
  public ThreadFactoryBuilder daemon(boolean daemon) {
    this.daemon = daemon;
    return this;
  }

  /**
   * 设置线程优先级
   *
   * @param priority 线程优先级（1-10）
   * @return 当前构建器实例
   * @throws IllegalArgumentException 如果优先级不在 Thread.MIN_PRIORITY(1) 和 Thread.MAX_PRIORITY(10) 之间
   */
  public ThreadFactoryBuilder priority(int priority) {
    if (priority < Thread.MIN_PRIORITY) {
      throw new IllegalArgumentException(
          String.format("Thread priority (%s) must be >= %s", priority, Thread.MIN_PRIORITY));
    }
    if (priority > Thread.MAX_PRIORITY) {
      throw new IllegalArgumentException(
          String.format("Thread priority (%s) must be <= %s", priority, Thread.MAX_PRIORITY));
    }
    this.priority = priority;
    return this;
  }

  /**
   * 设置线程的未捕获异常处理器
   *
   * @param uncaughtExceptionHandler 异常处理器
   * @return 当前构建器实例
   */
  public ThreadFactoryBuilder uncaughtExceptionHandler(
      Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
    this.uncaughtExceptionHandler = uncaughtExceptionHandler;
    return this;
  }

  /**
   * 构建配置好的 ThreadFactory 实例
   *
   * @return 配置好的 ThreadFactory 实例
   */
  @Override
  public ThreadFactory build() {
    return build(this);
  }

  /**
   * 内部构建方法，根据配置创建 ThreadFactory
   *
   * @param builder 构建器实例
   * @return 配置好的 ThreadFactory
   */
  private static ThreadFactory build(ThreadFactoryBuilder builder) {
    // 使用配置的工厂，如未指定则使用默认工厂
    final ThreadFactory backingThreadFactory =
        (null != builder.backingThreadFactory) ? builder.backingThreadFactory
            : Executors.defaultThreadFactory();

    final String namePrefix = builder.namePrefix;
    final Boolean daemon = builder.daemon;
    final Integer priority = builder.priority;
    final Thread.UncaughtExceptionHandler handler = builder.uncaughtExceptionHandler;
    // 若设置了名称前缀，则使用计数器进行线程编号
    final AtomicLong count = (null == namePrefix) ? null : new AtomicLong();

    return r -> {
      final Thread thread = backingThreadFactory.newThread(r);
      // 如果配置了前缀则设置线程名称
      if (null != namePrefix) {
        thread.setName(namePrefix + "-" + count.getAndIncrement());
      }
      // 如果配置了守护状态则设置
      if (null != daemon) {
        thread.setDaemon(daemon);
      }
      // 如果配置了优先级则设置
      if (null != priority) {
        thread.setPriority(priority);
      }
      // 如果配置了异常处理器则设置
      if (null != handler) {
        thread.setUncaughtExceptionHandler(handler);
      }
      return thread;
    };
  }
}
