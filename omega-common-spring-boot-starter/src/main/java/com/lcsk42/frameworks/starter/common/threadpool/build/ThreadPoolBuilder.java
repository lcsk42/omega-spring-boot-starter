package com.lcsk42.frameworks.starter.common.threadpool.build;

import com.lcsk42.frameworks.starter.designpattern.builder.Builder;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池构建器，提供流畅的 API 用于配置和创建 ThreadPoolExecutor 实例。 这是一个不可变的构建器，具有线程安全性。
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ThreadPoolBuilder implements Builder<ThreadPoolExecutor> {

    // 默认核心线程数为 CPU 核数的 5 倍（基于 20% 利用率计算）
    private int corePoolSize = calculateCoreNum();

    // 默认最大线程数为核心线程数的 1.5 倍
    private int maximumPoolSize = corePoolSize + (corePoolSize >> 1);

    // 默认线程保活时间为 30 秒
    private long keepAliveTime = 30 * 1_000L;

    // 默认时间单位为毫秒
    private TimeUnit timeUnit = TimeUnit.MILLISECONDS;

    // 默认工作队列为无界队列，容量 4096
    private BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(4096);

    // 默认拒绝策略为 AbortPolicy
    private RejectedExecutionHandler rejectedExecutionHandler =
            new ThreadPoolExecutor.AbortPolicy();

    // 默认线程类型为非守护线程
    private boolean isDaemon = false;

    // 线程名称前缀
    private String threadNamePrefix;

    // 线程工厂实例
    private ThreadFactory threadFactory;

    /**
     * 创建 ThreadPoolBuilder 实例的工厂方法
     *
     * @return 新的 ThreadPoolBuilder 实例
     */
    public static ThreadPoolBuilder builder() {
        return new ThreadPoolBuilder();
    }

    /**
     * 基于 CPU 核数和 20% 利用率计算默认核心线程数
     *
     * @return 计算得出的核心线程数
     */
    private int calculateCoreNum() {
        int cpuCoreNum = Runtime.getRuntime().availableProcessors();
        return new BigDecimal(cpuCoreNum).divide(new BigDecimal("0.2"), RoundingMode.HALF_UP)
                .intValue();
    }

    /**
     * 设置自定义的线程工厂
     *
     * @param threadFactory 线程工厂实例
     * @return 当前构建器实例（用于方法链式调用）
     */
    public ThreadPoolBuilder threadFactory(ThreadFactory threadFactory) {
        this.threadFactory = threadFactory;
        return this;
    }

    /**
     * 设置核心线程池大小
     *
     * @param corePoolSize 核心线程数量
     * @return 当前构建器实例（用于方法链式调用）
     * @throws IllegalArgumentException 如果 corePoolSize 为负数
     */
    public ThreadPoolBuilder corePoolSize(int corePoolSize) {
        if (corePoolSize < 0) {
            throw new IllegalArgumentException("Core pool size must be non-negative");
        }
        this.corePoolSize = corePoolSize;
        return this;
    }

    /**
     * 设置最大线程池大小。如果小于当前核心线程数， 则自动将核心线程数向下调整。
     *
     * @param maximumPoolSize 最大线程数量
     * @return 当前构建器实例（用于方法链式调用）
     * @throws IllegalArgumentException 如果 maximumPoolSize <=0 或小于核心线程数
     */
    public ThreadPoolBuilder maximumPoolSize(int maximumPoolSize) {
        if (maximumPoolSize <= 0) {
            throw new IllegalArgumentException("Maximum pool size must be positive");
        }
        this.maximumPoolSize = maximumPoolSize;
        if (maximumPoolSize < this.corePoolSize) {
            this.corePoolSize = maximumPoolSize;
        }
        return this;
    }

    /**
     * 为线程工厂设置基本属性
     *
     * @param threadNamePrefix 线程名称前缀
     * @param isDaemon 线程是否为守护线程
     * @return 当前构建器实例（用于方法链式调用）
     */
    public ThreadPoolBuilder threadFactory(String threadNamePrefix, Boolean isDaemon) {
        this.threadNamePrefix = threadNamePrefix;
        this.isDaemon = isDaemon;
        return this;
    }

    /**
     * 设置线程保活时间（使用默认时间单位毫秒）
     *
     * @param keepAliveTime 保活时长
     * @return 当前构建器实例（用于方法链式调用）
     */
    public ThreadPoolBuilder keepAliveTime(long keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
        return this;
    }

    /**
     * 设置线程保活时间（指定时间单位）
     *
     * @param keepAliveTime 保活时长
     * @param timeUnit 时间单位
     * @return 当前构建器实例（用于方法链式调用）
     */
    public ThreadPoolBuilder keepAliveTime(long keepAliveTime, TimeUnit timeUnit) {
        this.keepAliveTime = keepAliveTime;
        this.timeUnit = timeUnit;
        return this;
    }

    /**
     * 设置拒绝策略处理器
     *
     * @param rejectedExecutionHandler 拒绝策略处理器
     * @return 当前构建器实例（用于方法链式调用）
     */
    public ThreadPoolBuilder rejected(RejectedExecutionHandler rejectedExecutionHandler) {
        this.rejectedExecutionHandler = rejectedExecutionHandler;
        return this;
    }

    /**
     * 设置工作队列实现
     *
     * @param workQueue 工作队列实例
     * @return 当前构建器实例（用于方法链式调用）
     */
    public ThreadPoolBuilder workQueue(BlockingQueue<Runnable> workQueue) {
        this.workQueue = workQueue;
        return this;
    }

    /**
     * 构建 ThreadPoolExecutor 实例
     *
     * @return 配置好的 ThreadPoolExecutor 实例
     * @throws IllegalArgumentException 如果参数无效或线程名前缀为空
     */
    @Override
    public ThreadPoolExecutor build() {
        if (threadFactory == null) {
            Assert.hasLength(threadNamePrefix,
                    "The thread name prefix cannot be empty or an empty string.");
            threadFactory = ThreadFactoryBuilder.builder().prefix(threadNamePrefix).daemon(isDaemon)
                    .build();
        }
        ThreadPoolExecutor executorService;
        try {
            executorService = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime,
                    timeUnit, workQueue,
                    threadFactory, rejectedExecutionHandler);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Error creating thread pool parameter.", ex);
        }
        return executorService;
    }
}
