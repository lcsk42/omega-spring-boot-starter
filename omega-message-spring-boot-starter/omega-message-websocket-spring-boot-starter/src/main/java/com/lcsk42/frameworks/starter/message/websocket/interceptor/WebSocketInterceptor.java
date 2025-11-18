package com.lcsk42.frameworks.starter.message.websocket.interceptor;

import com.lcsk42.frameworks.starter.message.websocket.config.WebSocketConfiguration;
import com.lcsk42.frameworks.starter.message.websocket.service.WebSocketClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.lang.NonNull;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import java.util.Map;

/**
 * WebSocket 拦截器
 */
@RequiredArgsConstructor
public class WebSocketInterceptor extends HttpSessionHandshakeInterceptor {

    private final WebSocketConfiguration webSocketConfiguration;
    private final WebSocketClientService webSocketClientService;

    @Override
    public boolean beforeHandshake(@NonNull ServerHttpRequest request,
            @NonNull ServerHttpResponse response,
            @NonNull WebSocketHandler wsHandler,
            Map<String, Object> attributes) {
        String clientId = webSocketClientService.getClientId((ServletServerHttpRequest) request);
        attributes.put(webSocketConfiguration.getClientIdKey(), clientId);
        return true;
    }

    @Override
    public void afterHandshake(@NonNull ServerHttpRequest request,
            @NonNull ServerHttpResponse response,
            @NonNull WebSocketHandler wsHandler,
            Exception exception) {
        super.afterHandshake(request, response, wsHandler, exception);
    }
}
