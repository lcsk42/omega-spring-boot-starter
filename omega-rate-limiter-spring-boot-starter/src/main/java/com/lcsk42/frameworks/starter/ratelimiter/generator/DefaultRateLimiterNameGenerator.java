package com.lcsk42.frameworks.starter.ratelimiter.generator;

import com.lcsk42.frameworks.starter.core.constant.StringConstant;
import org.apache.commons.lang3.ClassUtils;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 默认限流器名称生成器
 */
public class DefaultRateLimiterNameGenerator implements RateLimiterNameGenerator {

    protected final ConcurrentHashMap<Method, String> nameMap = new ConcurrentHashMap<>();

    /**
     * 生成限流器名称
     *
     * @param target 目标对象
     * @param method 目标方法
     * @param args 方法参数
     * @return 生成的限流器名称
     */
    @Override
    public String generate(Object target, Method method, Object... args) {
        // 使用computeIfAbsent保证线程安全，避免重复计算
        return nameMap.computeIfAbsent(method, this::buildMethodSignature);
    }

    /**
     * 构建方法签名字符串
     *
     * @param method 目标方法
     * @return 方法签名字符串
     */
    private String buildMethodSignature(Method method) {
        // 预分配适当大小
        StringBuilder nameSb = new StringBuilder(64);

        // 添加类名
        nameSb.append(ClassUtils.getName(method.getDeclaringClass()))
                .append(StringConstant.DOT)
                .append(method.getName())
                .append(StringConstant.ROUND_BRACKET_START);

        // 添加参数类型描述
        for (Class<?> paramType : method.getParameterTypes()) {
            appendTypeDescriptor(nameSb, paramType);
        }

        nameSb.append(StringConstant.ROUND_BRACKET_END);
        return nameSb.toString();
    }

    /**
     * 追加类型描述符到字符串构建器
     *
     * @param sb 字符串构建器
     * @param paramType 参数类型
     */
    private void appendTypeDescriptor(StringBuilder sb, Class<?> paramType) {
        Class<?> type = paramType;

        // 处理数组类型
        while (type.isArray()) {
            sb.append(StringConstant.BRACKET_START);
            type = type.getComponentType();
        }

        // 处理基本类型和引用类型
        if (type.isPrimitive()) {
            sb.append(getPrimitiveTypeCode(type));
        } else {
            sb.append('L')
                    .append(ClassUtils.getName(type))
                    .append(StringConstant.SEMICOLON);
        }
    }

    /**
     * 获取基本类型的类型代码
     *
     * @param primitiveType 基本类型Class对象
     * @return 对应的类型代码字符
     * @throws IllegalArgumentException 如果类型不是基本类型
     */
    private char getPrimitiveTypeCode(Class<?> primitiveType) {
        if (primitiveType == null) {
            throw new IllegalArgumentException("Class parameter cannot be null");
        }

        if (primitiveType == Integer.TYPE) {
            return 'I';
        }
        if (primitiveType == Void.TYPE) {
            return 'V';
        }
        if (primitiveType == Boolean.TYPE) {
            return 'Z';
        }
        if (primitiveType == Byte.TYPE) {
            return 'B';
        }
        if (primitiveType == Character.TYPE) {
            return 'C';
        }
        if (primitiveType == Short.TYPE) {
            return 'S';
        }
        if (primitiveType == Double.TYPE) {
            return 'D';
        }
        if (primitiveType == Float.TYPE) {
            return 'F';
        }
        if (primitiveType == Long.TYPE) {
            return 'J';
        }

        throw new IllegalArgumentException(
                "Unsupported primitive type: " + primitiveType.getName());
    }
}
