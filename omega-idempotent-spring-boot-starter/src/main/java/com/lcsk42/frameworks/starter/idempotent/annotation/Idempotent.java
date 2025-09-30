package com.lcsk42.frameworks.starter.idempotent.annotation;

import com.lcsk42.frameworks.starter.idempotent.enums.IdempotentSceneEnum;
import com.lcsk42.frameworks.starter.idempotent.enums.IdempotentTypeEnum;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * 幂等注解
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Idempotent {

    /**
     * 名称
     */
    String name() default "";

    /**
     * 键（支持 Spring EL 表达式）
     */
    String key() default "";

    /**
     * 验证幂等类型，支持多种幂等方式
     * RestAPI 建议使用 {@link IdempotentTypeEnum#TOKEN} 或 {@link IdempotentTypeEnum#PARAM}
     * 其它类型幂等验证，使用 {@link IdempotentTypeEnum#SPEL}
     */
    IdempotentTypeEnum type() default IdempotentTypeEnum.PARAM;

    /**
     * 验证幂等场景，支持多种 {@link IdempotentSceneEnum}
     */
    IdempotentSceneEnum scene() default IdempotentSceneEnum.RESTAPI;

    /**
     * 超时时间(默认 1 分钟)
     */
    long timeout() default 60L;

    /**
     * 时间单位（默认：秒）
     */
    TimeUnit unit() default TimeUnit.SECONDS;

    /**
     * 提示信息
     */
    String message() default "请勿重复操作";

    /**
     * 设置防重令牌 Key 前缀
     */
    String uniqueKeyPrefix() default "";
}
