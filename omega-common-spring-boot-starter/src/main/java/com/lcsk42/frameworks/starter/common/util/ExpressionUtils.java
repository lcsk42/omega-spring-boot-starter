package com.lcsk42.frameworks.starter.common.util;

import com.lcsk42.frameworks.starter.common.expression.ExpressionEvaluator;
import com.lcsk42.frameworks.starter.common.expression.ExpressionInvokeContext;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;

/**
 * 表达式解析工具类
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExpressionUtils {

    /**
     * 解析
     *
     * @param script 表达式
     * @param target 目标对象
     * @param method 目标方法
     * @param args 方法参数
     * @return 解析结果
     */
    public static Object eval(String script, Object target, Method method, Object... args) {
        try {
            if (StringUtils.isBlank(script)) {
                return null;
            }
            ExpressionEvaluator expressionEvaluator = new ExpressionEvaluator(script, method);
            ExpressionInvokeContext invokeContext =
                    new ExpressionInvokeContext(method, args, target);
            return expressionEvaluator.apply(invokeContext);
        } catch (Exception e) {
            log.error("Error occurs when eval script \"{}\" in {} : {}", script, method,
                    e.getMessage(), e);
            return null;
        }
    }
}
