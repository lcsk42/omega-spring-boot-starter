package com.lcsk42.frameworks.starter.core.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.core.Ordered;

/**
 * 排序常量
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class OrderedConstant {

    /**
     * 过滤器
     */
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Filter {

        /**
         * Token 过滤器
         */
        public static final int TOKEN = Ordered.HIGHEST_PRECEDENCE;

        /**
         * API 加密过滤器顺序
         */
        public static final int API_ENCRYPT = Ordered.HIGHEST_PRECEDENCE + 100;

        /**
         * 链路追踪过滤器顺序
         */
        public static final int TRACE = Ordered.HIGHEST_PRECEDENCE + 200;

        /**
         * XSS 过滤器顺序
         */
        public static final int XSS = Ordered.LOWEST_PRECEDENCE - 200;

        /**
         * Request Id 处理器
         */
        public static final int REQUEST_ID = 0;

        /**
         * 日志过滤器顺序
         */
        public static final int LOG = Ordered.LOWEST_PRECEDENCE - 100;
    }

    /**
     * 拦截器
     */
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Interceptor {
        /**
         * 租户拦截器顺序
         */
        public static final int TENANT = Ordered.HIGHEST_PRECEDENCE + 100;

        /**
         * 认证拦截器顺序
         */
        public static final int AUTH = Ordered.HIGHEST_PRECEDENCE + 200;

        /**
         * 日志拦截器顺序
         */
        public static final int LOG = Ordered.LOWEST_PRECEDENCE - 100;
    }
}
