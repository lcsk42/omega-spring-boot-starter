package com.lcsk42.frameworks.starter.web.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于标记类或方法，表明返回值无需经过全局结果处理器转换。
 * <p>
 * 该注解的作用是让特定类或方法免于全局结果处理，
 * 确保其返回值不会被任何默认处理器修改或包装。
 * </p>
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CompatibleOutput {
}