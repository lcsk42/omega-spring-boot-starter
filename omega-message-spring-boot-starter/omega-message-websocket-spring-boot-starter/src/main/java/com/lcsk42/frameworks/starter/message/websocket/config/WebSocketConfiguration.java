package com.lcsk42.frameworks.starter.message.websocket.config;

import com.lcsk42.frameworks.starter.core.constant.StringConstant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * WebSocket 配置属性
 */
@Getter
@Setter
@NoArgsConstructor
@ConfigurationProperties(prefix = WebSocketConfiguration.PREFIX)
public class WebSocketConfiguration {
    public static final String PREFIX = "framework.message.web-socket";

    /**
     * 路径
     */
    private String path = "/websocket";

    /**
     * 允许跨域的域名
     */
    private List<String> allowedOrigins = List.of(StringConstant.ASTERISK);

    /**
     * 客户端 ID Key
     */
    private String clientIdKey = "CLIENT_ID";
}
