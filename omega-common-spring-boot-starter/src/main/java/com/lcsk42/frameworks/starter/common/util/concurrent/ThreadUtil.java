package com.lcsk42.frameworks.starter.common.util.concurrent;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * 线程操作工具类。
 * <p>
 * 提供线程休眠、任务执行与任务提交等线程管理和控制相关的辅助方法，
 * 是对 JDK 线程 API 与 {@link GlobalThreadPool} 全局线程池的便捷封装。
 * 该类为工具类，禁止实例化。
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ThreadUtil {

    /**
     * 暂停当前线程执行指定时间（毫秒）。
     * <p>
     * 通过 Lombok 的 {@link SneakyThrows} 静默处理 {@link InterruptedException}，
     * 调用方无需显式捕获中断异常。
     *
     * @param millis 暂停时间（毫秒），必须为非负数
     */
    @SneakyThrows(value = InterruptedException.class)
    public static void sleep(long millis) {
        Thread.sleep(millis);
    }

    /**
     * 按指定时间单位暂停当前线程执行。
     * <p>
     * 通过 Lombok 的 {@link SneakyThrows} 静默处理 {@link InterruptedException}，
     * 调用方无需显式捕获中断异常。
     *
     * @param timeout  暂停时长，必须为非负数
     * @param timeUnit 时间单位，不能为 null
     */
    @SneakyThrows(value = InterruptedException.class)
    public static void sleep(long timeout, TimeUnit timeUnit) {
        timeUnit.sleep(timeout);
    }

    /**
     * 使用全局线程池异步执行 Runnable 任务。
     * <p>
     * 该方法不返回执行结果，任务执行过程中抛出的异常由线程池的异常处理机制处理。
     *
     * @param runnable 待执行的任务，不能为 null
     */
    public static void execute(Runnable runnable) {
        execute(runnable, GlobalThreadPool.getExecutor());
    }

    /**
     * 使用指定线程池异步执行 Runnable 任务。
     * <p>
     * 该方法不返回执行结果，任务执行过程中抛出的异常由线程池的异常处理机制处理。
     *
     * @param runnable 待执行的任务，不能为 null
     * @param executor 执行任务使用的自定义线程池，不能为 null
     */
    public static void execute(Runnable runnable, ExecutorService executor) {
        executor.execute(runnable);
    }

    /**
     * 向全局线程池提交 Callable 任务并返回表示任务结果的 Future。
     * <p>
     * 调用方可通过返回的 {@link Future} 获取任务执行结果或取消任务。
     *
     * @param task 待执行的 Callable 任务，不能为 null
     * @param <T>  任务返回结果的类型
     * @return 表示任务结果的 Future 对象，可通过其获取执行结果
     */
    public static <T> Future<T> submit(Callable<T> task) {
        return submit(task, GlobalThreadPool.getExecutor());
    }

    /**
     * 向指定线程池提交 Callable 任务并返回表示任务结果的 Future。
     * <p>
     * 调用方可通过返回的 {@link Future} 获取任务执行结果或取消任务。
     *
     * @param task     待执行的 Callable 任务，不能为 null
     * @param executor 执行任务使用的自定义线程池，不能为 null
     * @param <T>      任务返回结果的类型
     * @return 表示任务结果的 Future 对象，可通过其获取执行结果
     */
    public static <T> Future<T> submit(Callable<T> task, ExecutorService executor) {
        return executor.submit(task);
    }

    /**
     * 向全局线程池提交 Runnable 任务并返回表示任务执行的 Future。
     * <p>
     * 返回的 Future 在任务正常完成后返回 null，调用方可通过其等待任务完成或取消任务。
     *
     * @param runnable 待执行的 Runnable 任务，不能为 null
     * @return 表示任务执行的 Future 对象，任务完成后返回 null
     */
    public static Future<?> submit(Runnable runnable) {
        return submit(runnable, GlobalThreadPool.getExecutor());
    }

    /**
     * 向指定线程池提交 Runnable 任务并返回表示任务执行的 Future。
     * <p>
     * 返回的 Future 在任务正常完成后返回 null，调用方可通过其等待任务完成或取消任务。
     *
     * @param runnable 待执行的 Runnable 任务，不能为 null
     * @param executor 执行任务使用的自定义线程池，不能为 null
     * @return 表示任务执行的 Future 对象，任务完成后返回 null
     */
    public static Future<?> submit(Runnable runnable, ExecutorService executor) {
        return executor.submit(runnable);
    }
}