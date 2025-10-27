package com.lcsk42.frameworks.starter.gateway.util;

import com.lcsk42.frameworks.starter.convention.model.Result;
import com.lcsk42.frameworks.starter.core.constant.HttpHeaderConstant;
import com.lcsk42.frameworks.starter.json.jackson.util.JacksonUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ServerUtil {

    /**
     * 从 ServerHttpRequest 请求头中获取 request ID (请求标识符)
     * 如果请求 ID 不存在，则返回默认的异常请求 ID
     *
     * @param request 需要获取 request ID 的 ServerHttpRequest 对象
     * @return request ID 或默认的异常 request ID (若找不到时)
     */
    public static String getRequestId(ServerHttpRequest request) {
        String requestId = request.getHeaders()
                .getFirst(HttpHeaderConstant.REQUEST_ID);

        return StringUtils.defaultIfBlank(requestId, HttpHeaderConstant.getRequestId());
    }

    /**
     * 将 JSON 格式的响应写入指定的 ServerHttpResponse 对象中，包含给定的状态码和消息内容
     *
     * @param response 需要写入响应的 ServerHttpResponse 对象
     * @param statusCode 需要设置的 HTTP 状态码
     * @param message 需要包含在响应中的消息内容
     * @param requestId 需要包含在响应中的 request ID
     * @return 当响应写入完成时返回 Mono 对象
     */
    public static Mono<Void> write(ServerHttpResponse response, HttpStatusCode statusCode,
            String message, String requestId) {
        response.setStatusCode(statusCode);
        String json = JacksonUtil.toJSON(
                Result.builder()
                        .code(statusCode.toString())
                        .message(message)
                        .build()
                        .withRequestId(requestId));

        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        if (StringUtils.isBlank(json)) {
            return response.setComplete();
        }
        DataBuffer buffer = response.bufferFactory().wrap(json.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }
}
