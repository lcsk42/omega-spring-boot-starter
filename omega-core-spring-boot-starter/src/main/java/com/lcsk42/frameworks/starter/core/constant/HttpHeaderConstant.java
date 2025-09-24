package com.lcsk42.frameworks.starter.core.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * 自定义 Http 请求头常量
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HttpHeaderConstant {
    /**
     * 用户 ID
     */
    public static final String USER_ID = "User-Id";

    /**
     * 请求 ID
     */
    public static final String REQUEST_ID = "Request-Id";

    public static String getClientRequestId(String requestId) {
        return "C-" + requestId;
    }

    public static String getGatewayRequestId() {
        return "G-" + UUID.randomUUID();
    }

    public static String getReturnRequestId() {
        return "R-" + UUID.randomUUID();
    }

    public static String getExceptionRequestId() {
        return "GE-" + UUID.randomUUID();
    }

    /**
     * 令牌
     */
    public static final String TOKEN = "Token";
}
