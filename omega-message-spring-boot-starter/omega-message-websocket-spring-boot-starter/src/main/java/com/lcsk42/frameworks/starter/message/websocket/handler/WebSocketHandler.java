package com.lcsk42.frameworks.starter.message.websocket.handler;

import com.lcsk42.frameworks.starter.message.websocket.config.WebSocketConfiguration;
import com.lcsk42.frameworks.starter.message.websocket.service.WebSocketSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;

/**
 * WebSocket 处理器
 */
@Slf4j
@RequiredArgsConstructor
public class WebSocketHandler extends TextWebSocketHandler {

    private final WebSocketConfiguration webSocketConfiguration;
    private final WebSocketSessionService webSocketSessionService;


    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, TextMessage message)
            throws Exception {
        String clientId = this.getClientId(session);
        log.info("WebSocket receive message. clientId: {}, message: {}.", clientId,
                message.getPayload());
        super.handleTextMessage(session, message);
    }

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) {
        String clientId = this.getClientId(session);
        webSocketSessionService.add(clientId, session);
        log.info("WebSocket client connect successfully. clientId: {}.", clientId);
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session,
            @NonNull CloseStatus status) {
        String clientId = this.getClientId(session);
        webSocketSessionService.delete(clientId);
        log.info("WebSocket client connect closed. clientId: {}.", clientId);
    }

    @Override
    public void handleTransportError(@NonNull WebSocketSession session,
            @NonNull Throwable exception) throws IOException {
        String clientId = this.getClientId(session);
        if (session.isOpen()) {
            session.close();
        }
        webSocketSessionService.delete(clientId);
    }

    /**
     * 获取客户端 ID
     *
     * @param session 会话
     * @return 客户端 ID
     */
    private String getClientId(WebSocketSession session) {
        return session.getAttributes()
                .get(webSocketConfiguration.getClientIdKey())
                .toString();
    }
}
