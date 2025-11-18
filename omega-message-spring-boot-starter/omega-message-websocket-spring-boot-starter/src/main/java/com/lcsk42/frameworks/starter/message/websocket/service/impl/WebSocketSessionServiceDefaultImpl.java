package com.lcsk42.frameworks.starter.message.websocket.service.impl;

import com.lcsk42.frameworks.starter.message.websocket.service.WebSocketSessionService;
import org.springframework.web.socket.WebSocketSession;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class WebSocketSessionServiceDefaultImpl implements WebSocketSessionService {
    private static final Map<String, WebSocketSession> SESSION_MAP = new ConcurrentHashMap<>();

    @Override
    public void add(String key, WebSocketSession session) {
        SESSION_MAP.put(key, session);
    }

    @Override
    public void delete(String key) {
        SESSION_MAP.remove(key);
    }

    @Override
    public WebSocketSession get(String key) {
        return SESSION_MAP.get(key);
    }

    @Override
    public Collection<WebSocketSession> listAll() {
        return SESSION_MAP.values();
    }

    @Override
    public Set<String> listAllSessionIds() {
        return SESSION_MAP.keySet();
    }
}
