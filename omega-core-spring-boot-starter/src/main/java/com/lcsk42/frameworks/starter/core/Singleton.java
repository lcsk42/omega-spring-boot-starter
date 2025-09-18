package com.lcsk42.frameworks.starter.core;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * 单例对象容器
 * <p>
 * 此工具类提供了线程安全的容器，用于管理通过字符串键标识的单例对象。在 Spring 或其他依赖注入框架范围之外，
 * 它对于缓存和检索延迟初始化的对象非常有用。
 * </p>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Singleton {

    // 用于按字符串键存储单例实例的线程安全映射
    private static final ConcurrentHashMap<String, Object> SINGLE_OBJECT_POOL = new ConcurrentHashMap<>();

    /**
     * 通过键从容器中检索单例对象
     *
     * @param key 与对象关联的键
     * @param <T> 期望的对象类型
     * @return 单例对象，如果未找到则返回 null
     */
    @SuppressWarnings("unchecked")
    public static <T> T get(String key) {
        Object result = SINGLE_OBJECT_POOL.get(key);
        return result == null ? null : (T) result;
    }

    /**
     * 通过键从容器中检索单例对象
     * <p>
     * 如果找不到对象，将使用提供的 {@link Supplier} 创建对象并存储在容器中
     *
     * @param key      与对象关联的键
     * @param supplier 当对象不存在时用于创建对象的提供者
     * @param <T>      期望的对象类型
     * @return 现有的或新创建的单例对象
     */
    @SuppressWarnings("unchecked")
    public static <T> T get(String key, Supplier<T> supplier) {
        Object result = SINGLE_OBJECT_POOL.get(key);
        if (result == null && (result = supplier.get()) != null) {
            SINGLE_OBJECT_POOL.put(key, result);
        }
        return result != null ? (T) result : null;
    }

    /**
     * 使用对象的类名作为键将对象放入容器
     *
     * @param value 要存储的对象
     */
    public static void put(Object value) {
        put(value.getClass().getName(), value);
    }

    /**
     * 使用指定的键将对象放入容器
     *
     * @param key   与对象关联的键
     * @param value 要存储的对象
     */
    public static void put(String key, Object value) {
        SINGLE_OBJECT_POOL.put(key, value);
    }
}
