package com.lcsk42.frameworks.starter.common.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * 线程操作工具类。 提供线程管理和控制相关的辅助方法。
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ThreadUtil {

  /**
   * 暂停当前线程执行指定时间（毫秒）。 使用 Lombok 的 @SneakyThrows 静默处理 InterruptedException。
   *
   * @param millis 暂停时间（毫秒）
   */
  @SneakyThrows(value = InterruptedException.class)
  public static void sleep(long millis) {
    Thread.sleep(millis);
  }

  /**
   * 按指定时间单位暂停当前线程执行。 使用 Lombok 的 @SneakyThrows 静默处理 InterruptedException。
   *
   * @param timeout 暂停时长
   * @param timeUnit 时间单位
   */
  @SneakyThrows(value = InterruptedException.class)
  public static void sleep(long timeout, TimeUnit timeUnit) {
    timeUnit.sleep(timeout);
  }

  /**
   * 使用全局线程池执行 Runnable 任务。 此方法是全局线程池执行器的便捷封装。
   *
   * @param runnable 待执行的任务
   */
  public static void execute(Runnable runnable) {
    GlobalThreadPool.execute(runnable);
  }

  /**
   * 向全局线程池提交 Callable 任务并返回表示结果的 Future。 此方法是全局线程池执行器的便捷封装。
   *
   * @param task 待执行的 Callable 任务
   * @param <T> 任务返回结果的类型
   * @return 表示任务结果的 Future 对象
   */
  public static <T> Future<T> submit(Callable<T> task) {
    return GlobalThreadPool.submit(task);
  }

  /**
   * 向全局线程池提交 Runnable 任务并返回表示执行的 Future。 此方法是全局线程池执行器的便捷封装。
   *
   * @param runnable 待执行的 Runnable 任务
   * @return 表示任务执行的 Future 对象
   */
  public static Future<?> submit(Runnable runnable) {
    return GlobalThreadPool.submit(runnable);
  }
}
