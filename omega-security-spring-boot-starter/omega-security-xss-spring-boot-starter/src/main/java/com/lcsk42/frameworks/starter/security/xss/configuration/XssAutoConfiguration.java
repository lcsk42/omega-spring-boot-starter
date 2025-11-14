package com.lcsk42.frameworks.starter.security.xss.configuration;

import com.lcsk42.frameworks.starter.core.constant.OrderedConstant;
import com.lcsk42.frameworks.starter.core.constant.StringConstant;
import com.lcsk42.frameworks.starter.security.xss.filter.XssFilter;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.DispatcherType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

@Slf4j
@AutoConfiguration
@ConditionalOnWebApplication
@EnableConfigurationProperties(XssProperties.class)
@ConditionalOnProperty(prefix = XssProperties.PREFIX, name = "enabled", havingValue = "true")
public class XssAutoConfiguration {

    /**
     * XSS 过滤器
     */
    @Bean
    public FilterRegistrationBean<XssFilter> xssFilter(XssProperties xssProperties) {
        FilterRegistrationBean<XssFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new XssFilter(xssProperties));
        registrationBean.setOrder(OrderedConstant.Filter.XSS);
        registrationBean.addUrlPatterns(StringConstant.PATH_PATTERN_CURRENT_DIR);
        registrationBean.setDispatcherTypes(DispatcherType.REQUEST);
        return registrationBean;
    }

    @PostConstruct
    public void postConstruct() {
        log.debug("[Omega] - Auto Configuration 'Security-XSS' completed initialization.");
    }
}
