package com.lcsk42.frameworks.starter.web.annotation;

import com.lcsk42.frameworks.starter.web.validator.EnumValueValidator;
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
 * 枚举校验注解
 *
 * <p>
 * {@code @EnumValue(value = XxxEnum.class, message = "参数值无效")} <br />
 * {@code @EnumValue(enumValues = {"F", "M"} ,message = "性别只允许为F或M")}
 * </p>
 *
 * @author Jasmine
 * @author Charles7c
 * @since 2.7.3
 */
@Documented
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EnumValueValidator.class)
public @interface EnumValue {

    /**
     * 枚举类
     *
     * @return 枚举类
     */
    Class<? extends Enum> value() default Enum.class;

    /**
     * 枚举值
     *
     * @return 枚举值
     */
    String[] enumValues() default {};

    /**
     * 获取枚举值的方法名
     *
     * @return 获取枚举值的方法名
     */
    String method() default "";

    /**
     * 提示消息
     *
     * @return 提示消息
     */
    String message() default "参数值无效";

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
