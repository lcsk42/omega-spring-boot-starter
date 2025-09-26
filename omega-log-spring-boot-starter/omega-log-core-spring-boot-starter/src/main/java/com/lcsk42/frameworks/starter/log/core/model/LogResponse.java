package com.lcsk42.frameworks.starter.log.core.model;

import com.lcsk42.frameworks.starter.json.jackson.util.JacksonUtil;
import com.lcsk42.frameworks.starter.log.core.enums.Include;
import com.lcsk42.frameworks.starter.log.core.util.LogUtil;
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
        this.status = LogUtil.getResponseStatus();

        // 返回的请求头
        if (LogUtil.contains(includes, Include.RESPONSE_HEADERS)) {
            this.headers = LogUtil.getResponseHeaders();
        }

        // 返回体
        if (LogUtil.contains(includes, Include.RESPONSE_BODY)) {
            this.body = LogUtil.getResponseBody();
        }

        // 返回参数
        if (LogUtil.contains(includes, Include.RESPONSE_PARAM)) {
            this.param = LogUtil.getResponseParams();
        }
    }

    @Override
    public String toString() {
        return JacksonUtil.toPrettyJson(this);
    }
}
