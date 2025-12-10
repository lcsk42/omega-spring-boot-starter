package com.lcsk42.frameworks.starter.idempotent.util;

import com.lcsk42.frameworks.starter.common.util.ExpressionUtils;
import com.lcsk42.frameworks.starter.core.ApplicationContextHolder;
import com.lcsk42.frameworks.starter.core.util.CacheUtil;
import com.lcsk42.frameworks.starter.idempotent.annotation.Idempotent;
import com.lcsk42.frameworks.starter.idempotent.exception.IdempotentException;
import com.lcsk42.frameworks.starter.idempotent.generator.IdempotentNameGenerator;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class KeyUtil {

    /**
     * 获取缓存 Key
     *
     * @param joinPoint 切点
     * @param idempotent 幂等注解
     * @return 缓存 Key
     */
    public static String getCacheKey(ProceedingJoinPoint joinPoint, Idempotent idempotent) {
        Object target = joinPoint.getTarget();
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        Object[] args = joinPoint.getArgs();
        // 获取名称
        String name = idempotent.name();
        if (StringUtils.isBlank(name)) {
            name = ApplicationContextHolder.getBean(IdempotentNameGenerator.class).generate(target,
                    method, args);
        }
        // 解析 Key
        String key = idempotent.key();
        if (StringUtils.isNotBlank(key)) {
            Object eval = ExpressionUtils.eval(key, target, method, args);
            if (eval == null) {
                throw new IdempotentException("Idempotency Key Parsing Error");
            }
            key = String.valueOf(eval);
        }

        return CacheUtil.buildKey(idempotent.uniqueKeyPrefix(), name, key);
    }
}
