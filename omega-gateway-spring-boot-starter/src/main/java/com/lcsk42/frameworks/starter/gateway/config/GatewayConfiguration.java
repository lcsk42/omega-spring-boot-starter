package com.lcsk42.frameworks.starter.gateway.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Configuration
@ConfigurationProperties(prefix = GatewayConfiguration.PREFIX)
public class GatewayConfiguration {

    public static final String PREFIX = "framework.gateway";

    /**
     * Token 的密钥
     */
    private String tokenSecret;

    /**
     * Paths that do not require authentication
     * 不需要身份验证的路径
     */
    private List<HttpEndpoint> allowList;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class HttpEndpoint {
        /**
         * HTTP method
         * HTTP 方法
         */
        private String method;

        /**
         * Path for the HTTP endpoint
         * /info, /info/, /info/**, etc.
         * HTTP 端点路径
         * 如：/info 、 /info/ 、 /info/** 等
         */
        private String path;
    }
}
