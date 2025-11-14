package com.lcsk42.frameworks.starter.security.mask.annotation;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.lcsk42.frameworks.starter.core.constant.StringConstant;
import com.lcsk42.frameworks.starter.security.mask.enums.MaskType;
import com.lcsk42.frameworks.starter.security.mask.serializer.JsonMaskSerializer;
import com.lcsk42.frameworks.starter.security.mask.strategy.MaskStrategy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * JSON 脱敏注解
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@JsonSerialize(using = JsonMaskSerializer.class)
public @interface JsonMask {
    /**
     * 脱敏类型
     */
    MaskType value() default MaskType.CUSTOM;

    /**
     * 脱敏策略
     * <p>
     * 优先级高于脱敏类型
     * </p>
     */
    Class<? extends MaskStrategy> strategy() default MaskStrategy.class;

    /**
     * 左侧保留位数
     * <p>
     * 仅在脱敏类型为 {@code MaskType.CUSTOM } 时使用
     * </p>
     */
    int left() default 0;

    /**
     * 右侧保留位数
     * <p>
     * 仅在脱敏类型为 {@code MaskType.CUSTOM } 时使用
     * </p>
     */
    int right() default 0;

    /**
     * 脱敏符号（默认：*）
     */
    char character() default StringConstant.ASTERISK_CHAR;
}
