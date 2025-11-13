package com.lcsk42.frameworks.starter.message.mail.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

@Getter
@Setter
@NoArgsConstructor
@ConfigurationProperties(prefix = MailConfiguration.PREFIX)
public class MailConfiguration {

    public static final String PREFIX = "framework.message.mail";

    public static final String DEFAULT_PROTOCOL = "smtp";

    /**
     * 协议
     */
    private String protocol = DEFAULT_PROTOCOL;

    /**
     * 服务器地址
     */
    private String host;

    /**
     * 服务器端口
     */
    private Integer port = 25;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码（授权码）
     */
    private String password;

    /**
     * 发件人
     */
    private String from;

    /**
     * 是否启用 SSL 连接
     */
    private Boolean sslEnabled = true;

    /**
     * 默认的编码格式
     */
    private Charset defaultEncoding = StandardCharsets.UTF_8;


    private Properties javaMailProperties = new Properties();
}
