package com.lcsk42.frameworks.starter.gateway.config;

import com.lcsk42.frameworks.starter.core.YamlPropertySourceFactory;
import com.lcsk42.frameworks.starter.gateway.controller.DevToolsController;
import com.lcsk42.frameworks.starter.gateway.filter.RequestIdFilter;
import com.lcsk42.frameworks.starter.gateway.filter.TokenValidateGlobalFilter;
import com.lcsk42.frameworks.starter.gateway.handler.GatewayExceptionHandler;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.web.reactive.error.ErrorWebFluxAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;

@Slf4j
@AutoConfigureBefore(ErrorWebFluxAutoConfiguration.class)
@EnableConfigurationProperties(GatewayConfiguration.class)
@RequiredArgsConstructor
@PropertySource(value = "classpath:default-gateway.yml", factory = YamlPropertySourceFactory.class)
public class GatewayAutoConfiguration {

    private final GatewayConfiguration gatewayConfiguration;

    /**
     * Request ID 过滤器
     */
    @Bean
    @ConditionalOnMissingBean
    public RequestIdFilter requestIdFilter() {
        return new RequestIdFilter();
    }

    /**
     * Token 过滤器
     */
    @Bean
    @ConditionalOnMissingBean
    public TokenValidateGlobalFilter tokenValidateGlobalFilter(
            GatewayConfiguration gatewayConfiguration) {
        return new TokenValidateGlobalFilter(gatewayConfiguration);
    }

    /**
     * Gateway 异常处理器
     *
     * @return gatewayExceptionHandler
     */
    @Bean
    @ConditionalOnMissingBean
    public GatewayExceptionHandler gatewayExceptionHandler() {
        return new GatewayExceptionHandler();
    }

    /**
     * 解决浏览器接口调试报错问题
     * .well-known/appspecific/com.chrome.devtools.json
     */
    @Bean
    public DevToolsController initializeDispatcherServletController() {
        return new DevToolsController();
    }

    @PostConstruct
    public void postConstruct() {
        log.debug("[Omega] - Auto Configuration 'Gateway' completed initialization.");
    }
}
