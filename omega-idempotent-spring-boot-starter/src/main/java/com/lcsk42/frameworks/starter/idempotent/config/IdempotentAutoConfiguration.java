package com.lcsk42.frameworks.starter.idempotent.config;

import com.lcsk42.frameworks.starter.cache.redisson.DistributedCache;
import com.lcsk42.frameworks.starter.cache.redisson.config.RedissonAutoConfiguration;
import com.lcsk42.frameworks.starter.idempotent.aop.IdempotentAspect;
import com.lcsk42.frameworks.starter.idempotent.generator.DefaultIdempotentNameGenerator;
import com.lcsk42.frameworks.starter.idempotent.generator.IdempotentNameGenerator;
import com.lcsk42.frameworks.starter.idempotent.impl.param.IdempotentParamExecuteHandler;
import com.lcsk42.frameworks.starter.idempotent.impl.param.IdempotentParamService;
import com.lcsk42.frameworks.starter.idempotent.impl.spel.IdempotentSpELByMQExecuteHandler;
import com.lcsk42.frameworks.starter.idempotent.impl.spel.IdempotentSpELByRestAPIExecuteHandler;
import com.lcsk42.frameworks.starter.idempotent.impl.token.IdempotentTokenController;
import com.lcsk42.frameworks.starter.idempotent.impl.token.IdempotentTokenExecuteHandler;
import com.lcsk42.frameworks.starter.idempotent.impl.token.IdempotentTokenService;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@Slf4j
@AllArgsConstructor
@AutoConfiguration(after = RedissonAutoConfiguration.class)
public class IdempotentAutoConfiguration {

    /**
     * 幂等切面
     */
    @Bean
    public IdempotentAspect idempotentAspect() {
        return new IdempotentAspect();
    }

    /**
     * 幂等名称生成器
     */
    @Bean
    @ConditionalOnMissingBean
    public IdempotentNameGenerator idempotentNameGenerator() {
        return new DefaultIdempotentNameGenerator();
    }

    /**
     * 参数方式幂等实现，基于 RestAPI 场景
     */
    @Bean
    @ConditionalOnMissingBean
    public IdempotentParamService idempotentParamExecuteHandler(RedissonClient redissonClient) {
        return new IdempotentParamExecuteHandler(redissonClient);
    }

    /**
     * Token 方式幂等实现，基于 RestAPI 场景
     */
    @Bean
    @ConditionalOnMissingBean
    public IdempotentTokenService idempotentTokenExecuteHandler(DistributedCache distributedCache) {
        return new IdempotentTokenExecuteHandler(distributedCache);
    }

    /**
     * 申请幂等 Token 控制器，基于 RestAPI 场景
     */
    @Bean
    public IdempotentTokenController idempotentTokenController(
            IdempotentTokenService idempotentTokenService) {
        return new IdempotentTokenController(idempotentTokenService);
    }

    /**
     * SpEL 方式幂等实现，基于 RestAPI 场景
     */
    @Bean
    @ConditionalOnMissingBean
    public IdempotentSpELByRestAPIExecuteHandler idempotentSpELByRestAPIExecuteHandler(
            RedissonClient redissonClient) {
        return new IdempotentSpELByRestAPIExecuteHandler(redissonClient);
    }

    /**
     * SpEL 方式幂等实现，基于 MQ 场景
     */
    @Bean
    @ConditionalOnMissingBean
    public IdempotentSpELByMQExecuteHandler idempotentSpELByMQExecuteHandler(
            DistributedCache distributedCache) {
        return new IdempotentSpELByMQExecuteHandler(distributedCache);
    }

    @PostConstruct
    public void postConstruct() {
        log.debug("[Omega] - Auto Configuration 'Idempotent' completed initialization.");
    }
}
