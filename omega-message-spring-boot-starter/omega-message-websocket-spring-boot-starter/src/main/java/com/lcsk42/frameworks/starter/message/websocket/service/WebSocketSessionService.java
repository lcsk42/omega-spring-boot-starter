package com.lcsk42.frameworks.starter.message.websocket.service;

import org.springframework.web.socket.WebSocketSession;

import java.util.Collection;
import java.util.Set;

/**
 * WebSocket 会话管理
 */
public interface WebSocketSessionService {
    /**
     * 添加会话
     *
     * @param key 会话 Key
     * @param session 会话信息
     */
    void add(String key, WebSocketSession session);

    /**
     * 删除会话
     *
     * @param key 会话 Key
     */
    void delete(String key);

    /**
     * 获取会话
     *
     * @param key 会话 Key
     * @return 会话信息
     */
    WebSocketSession get(String key);

    /**
     * 获取所有会话
     *
     * @return 所有会话
     */
    Collection<WebSocketSession> listAll();

    /**
     * 获取所有会话 ID
     *
     * @return 所有会话 ID
     */
    Set<String> listAllSessionIds();
}
