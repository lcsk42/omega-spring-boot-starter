package com.lcsk42.frameworks.starter.common.expression;

import lombok.Getter;

import java.lang.reflect.Method;
import java.util.function.Function;

/**
 * 表达式解析器
 */
@Getter
public class ExpressionEvaluator implements Function<Object, Object> {

    private final Function<Object, Object> evaluator;

    public ExpressionEvaluator(String script, Method defineMethod) {
        this.evaluator = new SpelEvaluator(script, defineMethod);
    }

    @Override
    public Object apply(Object rootObject) {
        return evaluator.apply(rootObject);
    }
}
