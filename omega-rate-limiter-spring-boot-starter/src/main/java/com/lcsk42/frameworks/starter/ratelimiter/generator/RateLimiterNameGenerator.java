package com.lcsk42.frameworks.starter.ratelimiter.generator;

import java.lang.reflect.Method;

/**
 * 限流器名称生成器
 */
public interface RateLimiterNameGenerator {

    /**
     * 为指定方法及其参数生成速率限制器名称
     *
     * @param target 目标实例
     * @param method 被调用的方法
     * @param args 方法参数（包含任意可变参数展开）
     * @return 生成的速率限制器名称
     */
    String generate(Object target, Method method, Object... args);
}
