package com.lcsk42.frameworks.starter.web.initialize;

import lombok.Getter;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Getter
@Component
public class PortHolder implements ApplicationListener<WebServerInitializedEvent> {

    /**
     * Web 服务器运行的端口号。
     * 该值在应用上下文初始化时被设置。
     */
    private int port;

    @Override
    public void onApplicationEvent(WebServerInitializedEvent event) {
        this.port = event.getWebServer().getPort();
    }
}