package com.lcsk42.frameworks.starter.web.annotation;

import com.lcsk42.frameworks.starter.web.validator.JsonStringValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;

/**
 * JSON 格式字符串校验注解
 *
 * <p>
 * 校验字符串是否为 JSON 格式字符串
 * {@code @JsonString(message = "必须为有效的 JSON 格式")} <br />
 * </p>
 *
 * @author Charles7c
 * @since 2.12.0
 */
@Documented
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = JsonStringValidator.class)
public @interface JsonString {

    /**
     * 提示消息
     *
     * @return 提示消息
     */
    String message() default "必须为有效的 JSON 格式";

    /**
     * 分组
     *
     * @return 分组
     */
    Class<?>[] groups() default {};

    /**
     * 负载
     *
     * @return 负载
     */
    Class<? extends Payload>[] payload() default {};
}