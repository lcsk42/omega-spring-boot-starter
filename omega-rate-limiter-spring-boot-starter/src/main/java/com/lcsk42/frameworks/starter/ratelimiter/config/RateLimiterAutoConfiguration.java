package com.lcsk42.frameworks.starter.ratelimiter.config;

import com.lcsk42.frameworks.starter.cache.redisson.config.RedissonAutoConfiguration;
import com.lcsk42.frameworks.starter.ratelimiter.aop.RateLimiterAspect;
import com.lcsk42.frameworks.starter.ratelimiter.generator.DefaultRateLimiterNameGenerator;
import com.lcsk42.frameworks.starter.ratelimiter.generator.RateLimiterNameGenerator;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * 限流器自动配置
 */
@Slf4j
@AutoConfiguration(after = RedissonAutoConfiguration.class)
@EnableConfigurationProperties(RateLimiterConfiguration.class)
public class RateLimiterAutoConfiguration {
    /**
     * 限流器切面
     */
    @Bean
    public RateLimiterAspect rateLimiterAspect(RateLimiterConfiguration configuration,
            RateLimiterNameGenerator rateLimiterNameGenerator,
            RedissonClient redissonClient) {
        return new RateLimiterAspect(configuration, rateLimiterNameGenerator, redissonClient);
    }

    /**
     * 限流器名称生成器
     */
    @Bean
    @ConditionalOnMissingBean
    public RateLimiterNameGenerator nameGenerator() {
        return new DefaultRateLimiterNameGenerator();
    }

    @PostConstruct
    public void postConstruct() {
        log.debug("[Omega] - Auto Configuration 'RateLimiter' completed initialization.");
    }
}
