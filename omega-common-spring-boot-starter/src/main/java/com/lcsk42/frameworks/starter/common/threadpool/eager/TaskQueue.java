package com.lcsk42.frameworks.starter.common.threadpool.eager;

import lombok.NonNull;
import lombok.Setter;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * 专为线程池快速消费任务设计的阻塞队列。 与 EagerThreadPoolExecutor 协同工作以优化线程创建。
 */
@Setter
public class TaskQueue extends LinkedBlockingQueue<Runnable> {
    // 关联的线程池执行器引用
    private EagerThreadPoolExecutor executor;

    /**
     * 构造指定容量的任务队列
     *
     * @param capacity 队列的最大容量
     */
    public TaskQueue(int capacity) {
        super(capacity);
    }

    /**
     * 向队列提交任务，包含线程池优化的特殊处理逻辑。 行为根据当前线程池状态有所不同： - 当核心线程可用时优先排队 - 低于最大线程数时强制创建新线程 - 达到最大容量时回退到普通排队
     *
     * @param runnable 待执行的任务
     * @return 成功入队返回 true，否则 false
     * @throws NullPointerException 如果任务为 null
     */
    @Override
    public boolean offer(@NonNull Runnable runnable) {
        int currentPoolThreadSize = executor.getPoolSize();

        // 当存在空闲核心线程时，直接将任务加入队列供核心线程处理
        if (executor.getSubmittedTaskCount() < currentPoolThreadSize) {
            return super.offer(runnable);
        }

        // 当低于最大线程数时返回 false，触发创建非核心线程（根据线程池逻辑）
        if (currentPoolThreadSize < executor.getMaximumPoolSize()) {
            return false;
        }

        // 达到最大线程数时，回退到普通排队行为
        return super.offer(runnable);
    }

    /**
     * 尝试在超时时间内将任务提交到队列。 主要用于任务拒绝处理时的重试操作。
     *
     * @param o 待执行的任务
     * @param timeout 超时等待时长
     * @param unit 时间单位
     * @return 成功返回 true，超时返回 false
     * @throws InterruptedException 如果等待期间被中断
     * @throws RejectedExecutionException 如果执行器已关闭
     */
    public boolean retryOffer(Runnable o, long timeout, TimeUnit unit) throws InterruptedException {
        if (executor.isShutdown()) {
            throw new RejectedExecutionException("Executor is shutdown!");
        }
        return super.offer(o, timeout, unit);
    }
}
