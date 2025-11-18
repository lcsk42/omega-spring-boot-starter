package com.lcsk42.frameworks.starter.message.websocket.util;

import com.lcsk42.frameworks.starter.core.ApplicationContextHolder;
import com.lcsk42.frameworks.starter.message.websocket.service.WebSocketSessionService;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.List;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class WebSocketUtil {
    private static final WebSocketSessionService SESSION_SERVICE =
            ApplicationContextHolder.getBean(WebSocketSessionService.class);

    /**
     * 发送消息
     *
     * @param clientId 客户端 ID
     * @param message 消息内容
     */
    public static void sendMessage(String clientId, String message) {
        WebSocketSession session = SESSION_SERVICE.get(clientId);
        sendMessage(session, message);
    }

    /**
     * 发送消息
     *
     * @param session 会话
     * @param message 消息内容
     */
    public static void sendMessage(WebSocketSession session, String message) {
        sendMessage(session, new TextMessage(message));
    }

    /**
     * 批量发送消息
     *
     * @param clientIds 客户端 ID 列表
     * @param message 消息内容
     */
    public static void sendMessage(List<String> clientIds, String message) {
        SESSION_SERVICE.listAllSessionIds()
                .parallelStream()
                .filter(clientIds::contains)
                .forEach(sessionId -> sendMessage(sessionId, message));
    }

    /**
     * 发送消息给所有客户端
     *
     * @param message 消息内容
     */
    public static void sendMessage(String message) {
        SESSION_SERVICE.listAll()
                .parallelStream()
                .forEach(session -> sendMessage(session, message));
    }

    /**
     * 发送消息
     *
     * @param session 会话
     * @param message 消息内容
     */
    public static void sendMessage(WebSocketSession session, WebSocketMessage<?> message) {
        if (session == null || !session.isOpen()) {
            log.warn("WebSocket session closed.");
            return;
        }
        try {
            session.sendMessage(message);
        } catch (IOException e) {
            log.error("WebSocket send message failed. sessionId: {}.", session.getId(), e);
        }
    }
}
