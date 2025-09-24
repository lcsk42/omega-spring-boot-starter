package com.lcsk42.frameworks.starter.designpattern.config;

import com.lcsk42.frameworks.starter.core.config.CoreAutoConfiguration;
import com.lcsk42.frameworks.starter.designpattern.chain.AbstractChainContext;
import com.lcsk42.frameworks.starter.designpattern.strategy.AbstractStrategyChoose;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * Design Pattern Auto Configuration
 */
@Slf4j
@ImportAutoConfiguration(CoreAutoConfiguration.class)
public class DesignPatternAutoConfiguration {

    /**
     * 策略模式选择器
     */
    @Bean
    public AbstractStrategyChoose abstractStrategyChoose() {
        return new AbstractStrategyChoose();
    }

    /**
     * 责任链模式上下文
     */
    @SuppressWarnings("rawtypes")
    @Bean
    public AbstractChainContext abstractChainContext() {
        return new AbstractChainContext();
    }

    @PostConstruct
    public void postConstruct() {
        log.debug("[Omega] - Auto Configuration 'Design Pattern' completed initialization.");
    }
}
