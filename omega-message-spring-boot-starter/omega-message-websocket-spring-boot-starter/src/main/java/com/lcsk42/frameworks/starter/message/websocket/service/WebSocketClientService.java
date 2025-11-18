package com.lcsk42.frameworks.starter.message.websocket.service;

import org.springframework.http.server.ServletServerHttpRequest;

/**
 * WebSocket 客户端服务
 */
public interface WebSocketClientService {

    /**
     * 获取当前客户端 ID
     *
     * @param request 请求对象
     * @return 当前客户端 ID
     */
    String getClientId(ServletServerHttpRequest request);
}
