package com.lcsk42.frameworks.starter.log.aop.config;

import com.lcsk42.frameworks.starter.core.ApplicationContextHolder;
import com.lcsk42.frameworks.starter.core.constant.OrderedConstant;
import com.lcsk42.frameworks.starter.core.constant.StringConstant;
import com.lcsk42.frameworks.starter.log.aop.aspect.AccessLogAspect;
import com.lcsk42.frameworks.starter.log.aop.aspect.LogAspect;
import com.lcsk42.frameworks.starter.log.aop.handler.AopLogHandler;
import com.lcsk42.frameworks.starter.log.core.config.LogProperties;
import com.lcsk42.frameworks.starter.log.core.filter.LogFilter;
import com.lcsk42.frameworks.starter.log.core.handler.LogHandler;
import com.lcsk42.frameworks.starter.log.core.service.LogService;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.DispatcherType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@EnableConfigurationProperties(LogProperties.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@RequiredArgsConstructor
public class AopLogAutoConfiguration {
    private final LogProperties logProperties;
    private final LogHandler logHandler;

    /**
     * 日志过滤器
     */
    @Bean
    @ConditionalOnMissingBean
    public FilterRegistrationBean<LogFilter> logFilter() {
        FilterRegistrationBean<LogFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new LogFilter(logProperties));
        registrationBean.setOrder(OrderedConstant.Filter.LOG);
        registrationBean.addUrlPatterns(StringConstant.PATH_PATTERN_CURRENT_DIR);
        registrationBean.setDispatcherTypes(DispatcherType.REQUEST);
        return registrationBean;
    }

    /**
     * 日志切面
     *
     * @return {@link LogAspect }
     */
    @Bean
    @ConditionalOnMissingBean
    public LogAspect logAspect() {
        return new LogAspect(logProperties, logHandler, ApplicationContextHolder.getBean(LogService.class));
    }

    /**
     * 访问日志切面
     *
     * @return {@link AccessLogAspect }
     */
    @Bean
    @ConditionalOnMissingBean
    public AccessLogAspect accessLogAspect() {
        return new AccessLogAspect(logProperties, logHandler);
    }

    /**
     * 日志处理器
     */
    @Bean
    @ConditionalOnMissingBean
    public LogHandler logHandler() {
        return new AopLogHandler();
    }

    @PostConstruct
    public void postConstruct() {
        log.debug("[Omega] - Auto Configuration 'Log-AOP' completed initialization.");
    }
}
