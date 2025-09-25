package com.lcsk42.frameworks.starter.log.core.model;

import com.lcsk42.frameworks.starter.log.core.enums.Include;
import com.lcsk42.frameworks.starter.log.core.util.AccessLogUtil;
import lombok.Getter;

import java.util.Map;
import java.util.Set;

/**
 * 响应信息
 */
@Getter
public class LogResponse {

    /**
     * 状态码
     */
    private final Integer status;

    /**
     * 响应头
     */
    private Map<String, String> headers;

    /**
     * 响应体（JSON 字符串）
     */
    private String body;

    /**
     * 响应参数
     */
    private Map<String, Object> param;

    public LogResponse(Set<Include> includes) {
        // 返回状态
        this.status = AccessLogUtil.getResponseStatus();

        // 返回的请求头
        if (includes.contains(Include.RESPONSE_HEADERS)) {
            this.headers = AccessLogUtil.getResponseHeaders();
        }

        // 返回体
        if (includes.contains(Include.RESPONSE_BODY)) {
            this.body = AccessLogUtil.getRequestBody();
        }

        // 返回参数
        if (includes.contains(Include.RESPONSE_PARAM)) {
            this.param = AccessLogUtil.getResponseParams();
        }
    }
}
