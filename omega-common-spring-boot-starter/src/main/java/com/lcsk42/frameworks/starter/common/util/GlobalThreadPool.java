package com.lcsk42.frameworks.starter.common.util;

import com.lcsk42.frameworks.starter.common.threadpool.build.ThreadPoolBuilder;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GlobalThreadPool {

  /**
   * 全局线程池执行器。 这是一个可在整个应用程序中使用的单例实例。
   */
  private static ThreadPoolExecutor threadPoolExecutor;

  /**
   * 初始化全局线程池执行器。 该方法应在应用程序启动时调用一次以设置线程池。
   */
  public static synchronized void init() {
    if (threadPoolExecutor != null) {
      threadPoolExecutor.shutdownNow();
    }

    threadPoolExecutor = ThreadPoolBuilder.builder().threadFactory("global-", false).build();
  }

  /**
   * 关闭全局线程池执行器。 如果 isNow 为 true，将立即停止所有正在执行的任务； 否则将等待已提交的任务执行完成后再关闭。
   *
   * @param isNow 是否立即关闭（true 为立即关闭，false 为优雅关闭）
   */
  public static synchronized void shutdown(boolean isNow) {
    if (null != threadPoolExecutor) {
      if (isNow) {
        threadPoolExecutor.shutdownNow();
      } else {
        threadPoolExecutor.shutdown();
      }
    }
  }

  /**
   * 获取全局线程池执行器实例。 该方法用于访问线程池以执行任务。
   *
   * @return 全局线程池执行器实例
   */
  public static ExecutorService getExecutor() {
    return threadPoolExecutor;
  }

  /**
   * 在全局线程池中执行 Runnable 任务。 该方法将 Runnable 提交给线程池执行。
   *
   * @param runnable 待执行的任务
   */
  public static void execute(@NonNull Runnable runnable) {
    threadPoolExecutor.execute(runnable);
  }

  /**
   * 向全局线程池提交 Callable 任务。 该方法返回一个 Future，可用于获取任务的执行结果。
   *
   * @param task 待提交的 Callable 任务
   * @param <T> 任务结果的返回类型
   * @return 表示任务待定结果的 Future 对象
   */
  public static <T> Future<T> submit(@NonNull Callable<T> task) {
    return threadPoolExecutor.submit(task);
  }

  /**
   * 向全局线程池提交 Runnable 任务。 该方法返回一个 Future，可用于跟踪任务的执行状态。
   *
   * @param runnable 待提交的 Runnable 任务
   * @return 表示任务待定结果的 Future 对象
   */
  public static Future<?> submit(@NonNull Runnable runnable) {
    return threadPoolExecutor.submit(runnable);
  }

  static {
    init();
  }
}
