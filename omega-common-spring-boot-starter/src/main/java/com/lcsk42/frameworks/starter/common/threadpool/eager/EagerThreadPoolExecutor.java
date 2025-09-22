package com.lcsk42.frameworks.starter.common.threadpool.eager;

import org.springframework.lang.NonNull;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 专为快速消费任务设计的线程池执行器。 跟踪已提交任务数量并提供增强的拒绝处理机制。
 */
public class EagerThreadPoolExecutor extends ThreadPoolExecutor {

  // 原子计数器，用于跟踪已提交任务数量
  private final AtomicInteger submittedTaskCount = new AtomicInteger(0);

  /**
   * 使用给定参数构造新的 EagerThreadPoolExecutor
   *
   * @param corePoolSize 池中保持的线程数
   * @param maximumPoolSize 池中最大线程数
   * @param keepAliveTime 多余空闲线程等待新任务的时长
   * @param unit 时间单位
   * @param workQueue 任务执行前存放的队列
   * @param threadFactory 创建新线程的工厂
   * @param handler 执行被阻塞时使用的处理器
   */
  public EagerThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime,
      TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory,
      RejectedExecutionHandler handler) {
    super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);

    if (workQueue instanceof TaskQueue taskQueue) {
      taskQueue.setExecutor(this);
    }
  }

  /**
   * 获取当前已提交任务数量
   *
   * @return 已提交任务计数
   */
  public int getSubmittedTaskCount() {
    return submittedTaskCount.get();
  }

  /**
   * 任务完成执行后的钩子方法。 递减已提交任务计数器。
   */
  @Override
  protected void afterExecute(Runnable r, Throwable t) {
    submittedTaskCount.decrementAndGet();
  }

  /**
   * 执行给定任务，递增已提交任务计数。 通过尝试重新将任务放入队列提供增强的拒绝处理。
   *
   * @param command 要执行的任务
   * @throws RejectedExecutionException 如果任务无法被接受执行
   * @throws NullPointerException 如果命令为 null
   */
  @Override
  public void execute(@NonNull Runnable command) {
    submittedTaskCount.incrementAndGet();
    try {
      super.execute(command);
    } catch (RejectedExecutionException ex) {
      // TaskQueue 特殊处理：尝试再次提交任务
      TaskQueue taskQueue = (TaskQueue) super.getQueue();
      try {
        if (!taskQueue.retryOffer(command, 0, TimeUnit.MILLISECONDS)) {
          submittedTaskCount.decrementAndGet();
          throw new RejectedExecutionException("Queue capacity is full.", ex);
        }
      } catch (InterruptedException iex) {
        submittedTaskCount.decrementAndGet();
        Thread.currentThread().interrupt();
        throw new RejectedExecutionException(iex);
      }
    } catch (Exception ex) {
      submittedTaskCount.decrementAndGet();
      throw ex;
    }
  }
}
