package com.lcsk42.frameworks.starter.crypto.api.config;

import com.lcsk42.frameworks.starter.core.constant.OrderedConstant;
import com.lcsk42.frameworks.starter.core.constant.StringConstant;
import com.lcsk42.frameworks.starter.crypto.api.filter.ApiCryptoFilter;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.DispatcherType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

/**
 * API 加密自动配置
 */
@Slf4j
@AutoConfiguration
@EnableConfigurationProperties(ApiCryptoProperties.class)
@ConditionalOnProperty(prefix = ApiCryptoProperties.PREFIX, name = "enabled", havingValue = "true",
        matchIfMissing = true)
public class ApiCryptoAutoConfiguration {
    /**
     * API 加密过滤器
     */
    @Bean
    public FilterRegistrationBean<ApiCryptoFilter> apiCryptoFilterFilter(
            ApiCryptoProperties properties) {
        FilterRegistrationBean<ApiCryptoFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new ApiCryptoFilter(properties));
        registrationBean.setOrder(OrderedConstant.Filter.API_ENCRYPT);
        registrationBean.addUrlPatterns(StringConstant.PATH_PATTERN_CURRENT_DIR);
        registrationBean.setDispatcherTypes(DispatcherType.REQUEST);
        return registrationBean;
    }

    @PostConstruct
    public void postConstruct() {
        log.debug("[Omega] - Auto Configuration 'Crypto-API' completed initialization.");
    }
}
