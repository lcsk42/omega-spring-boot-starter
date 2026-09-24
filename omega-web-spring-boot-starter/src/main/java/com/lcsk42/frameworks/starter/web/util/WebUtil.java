package com.lcsk42.frameworks.starter.web.util;

import com.lcsk42.frameworks.starter.core.ApplicationContextHolder;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.server.PathContainer;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

import java.util.Arrays;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class WebUtil {

    /**
     * 路径是否匹配
     *
     * @param path 路径
     * @param patterns 匹配模式列表
     * @return 是否匹配
     */
    public static boolean isMatch(String path, List<String> patterns) {
        return patterns.stream().anyMatch(pattern -> isMatch(path, pattern));
    }

    /**
     * 路径是否匹配
     *
     * @param path 路径
     * @param patterns 匹配模式列表
     * @return 是否匹配
     */
    public static boolean isMatch(String path, String... patterns) {
        return Arrays.stream(patterns).anyMatch(pattern -> isMatch(path, pattern));
    }

    /**
     * 路径是否匹配
     *
     * @param path 路径
     * @param pattern 匹配模式
     * @return 是否匹配
     */
    public static boolean isMatch(String path, String pattern) {
        PathPattern pathPattern = PathPatternParser.defaultInstance.parse(pattern);
        PathContainer pathContainer = PathContainer.parsePath(path);
        return pathPattern.matches(pathContainer);
    }

    /**
     * 获取处理器方法
     *
     * @param request 请求
     * @return 处理器方法
     * @since 2.14.0
     */
    public static HandlerMethod getHandlerMethod(HttpServletRequest request) {
        try {
            RequestMappingHandlerMapping handlerMapping = ApplicationContextHolder
                    .getBean("requestMappingHandlerMapping", RequestMappingHandlerMapping.class);
            HandlerExecutionChain handlerExecutionChain = handlerMapping.getHandler(request);
            // 检查是否存在处理链
            if (handlerExecutionChain == null) {
                return null;
            }
            // 获取处理器
            Object handler = handlerExecutionChain.getHandler();
            if (handler instanceof HandlerMethod handlerMethod) {
                return handlerMethod;
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}
