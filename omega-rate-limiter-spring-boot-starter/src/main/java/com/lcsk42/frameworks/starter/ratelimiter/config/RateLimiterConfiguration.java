package com.lcsk42.frameworks.starter.ratelimiter.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@NoArgsConstructor
@Configuration
@ConfigurationProperties(prefix = RateLimiterConfiguration.PREFIX)
public class RateLimiterConfiguration {

    public static final String PREFIX = "framework.rate-limiter";

    /**
     * Key 前缀
     */
    private String keyPrefix = "rate-limiter";
}
