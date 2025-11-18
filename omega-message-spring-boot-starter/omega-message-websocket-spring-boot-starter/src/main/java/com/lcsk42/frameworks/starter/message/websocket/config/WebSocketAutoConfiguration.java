package com.lcsk42.frameworks.starter.message.websocket.config;

import com.lcsk42.frameworks.starter.message.websocket.interceptor.WebSocketInterceptor;
import com.lcsk42.frameworks.starter.message.websocket.service.WebSocketClientService;
import com.lcsk42.frameworks.starter.message.websocket.service.WebSocketSessionService;
import com.lcsk42.frameworks.starter.message.websocket.service.impl.WebSocketSessionServiceDefaultImpl;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.server.HandshakeInterceptor;

@Slf4j
@AllArgsConstructor
@EnableWebSocket
@EnableConfigurationProperties({WebSocketConfiguration.class})
public class WebSocketAutoConfiguration {

    private final WebSocketConfiguration webSocketConfiguration;

    @Bean
    public WebSocketConfigurer webSocketConfigurer(WebSocketHandler handler,
            HandshakeInterceptor interceptor) {
        return registry -> registry.addHandler(handler, webSocketConfiguration.getPath())
                .addInterceptors(interceptor)
                .setAllowedOrigins(
                        webSocketConfiguration.getAllowedOrigins().toArray(String[]::new));
    }

    @Bean
    @ConditionalOnMissingBean
    public WebSocketHandler webSocketHandler(WebSocketSessionService webSocketSessionService) {
        return new com.lcsk42.frameworks.starter.message.websocket.handler.WebSocketHandler(
                webSocketConfiguration, webSocketSessionService);
    }

    @Bean
    @ConditionalOnMissingBean
    public HandshakeInterceptor handshakeInterceptor(
            WebSocketClientService webSocketClientService) {
        return new WebSocketInterceptor(webSocketConfiguration, webSocketClientService);
    }

    /**
     * WebSocket 会话 DAO
     */
    @Bean
    @ConditionalOnMissingBean
    public WebSocketSessionService webSocketSessionService() {
        return new WebSocketSessionServiceDefaultImpl();
    }

    /**
     * WebSocket 客户端服务（如不提供，则报错）
     */
    @Bean
    @ConditionalOnMissingBean
    public WebSocketClientService webSocketClientService() {
        throw new NoSuchBeanDefinitionException(WebSocketClientService.class);
    }

    @PostConstruct
    public void postConstruct() {
        log.debug("[Omega] - Auto Configuration 'WebSocket' completed initialization.");
    }
}
