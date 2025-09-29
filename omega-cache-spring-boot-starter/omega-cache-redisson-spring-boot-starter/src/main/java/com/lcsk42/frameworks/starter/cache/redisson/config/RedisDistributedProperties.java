package com.lcsk42.frameworks.starter.cache.redisson.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Getter
@Setter
@NoArgsConstructor
@ConfigurationProperties(prefix = RedisDistributedProperties.PREFIX)
public class RedisDistributedProperties {

    public static final String PREFIX = "framework.cache.redis";

    /**
     * 键前缀
     */
    private String prefix = "";

    /**
     * 键前缀使用的字符集
     */
    private String prefixCharset = StandardCharsets.UTF_8.name();

    /**
     * 默认的时间间隔(30s)
     */
    private Duration timeout = Duration.ofSeconds(30);
}
