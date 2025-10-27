package com.lcsk42.frameworks.starter.log.interceptor.config;

import com.lcsk42.frameworks.starter.core.constant.OrderedConstant;
import com.lcsk42.frameworks.starter.core.constant.StringConstant;
import com.lcsk42.frameworks.starter.log.core.config.LogProperties;
import com.lcsk42.frameworks.starter.log.core.filter.LogFilter;
import com.lcsk42.frameworks.starter.log.core.handler.LogHandler;
import com.lcsk42.frameworks.starter.log.core.service.LogService;
import com.lcsk42.frameworks.starter.log.core.service.impl.DefaultLogServiceImpl;
import com.lcsk42.frameworks.starter.log.interceptor.handler.InterceptorLogHandler;
import com.lcsk42.frameworks.starter.log.interceptor.interceptor.LogInterceptor;
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
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Slf4j
@Configuration
@EnableConfigurationProperties(LogProperties.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@RequiredArgsConstructor
public class InterceptorLogAutoConfiguration implements WebMvcConfigurer {

    private final LogProperties logProperties;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LogInterceptor(logProperties, logHandler(), logService()))
                .addPathPatterns(StringConstant.PATH_PATTERN)
                .excludePathPatterns(logProperties.getExcludePatterns())
                .order(OrderedConstant.Interceptor.LOG);
    }

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
     * 日志处理器
     */
    @Bean
    @ConditionalOnMissingBean
    public LogHandler logHandler() {
        return new InterceptorLogHandler();
    }

    /**
     * 日志持久层接口
     */
    @Bean
    @ConditionalOnMissingBean
    public LogService logService() {
        return new DefaultLogServiceImpl();
    }

    @PostConstruct
    public void postConstruct() {
        log.debug("[Omega] - Auto Configuration 'Log-AOP' completed initialization.");
    }
}
