package com.lcsk42.frameworks.starter.crypto.api.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@NoArgsConstructor
@Configuration
@ConfigurationProperties(ApiCryptoProperties.PREFIX)
public class ApiCryptoProperties {
    public static final String PREFIX = "framework.crypto.api-crypto";

    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 请求头中 AES 密钥 键名
     */
    private String secretKeyHeader = "X-Api-Crypto";

    /**
     * 响应加密公钥
     */
    private String publicKey;

    /**
     * 请求解密私钥
     */
    private String privateKey;
}
