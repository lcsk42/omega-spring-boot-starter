package com.lcsk42.frameworks.starter.web.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.server.PathContainer;
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
}
