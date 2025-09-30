package com.lcsk42.frameworks.starter.web.config;

import com.lcsk42.frameworks.starter.core.YamlPropertySourceFactory;
import com.lcsk42.frameworks.starter.web.GlobalExceptionHandler;
import com.lcsk42.frameworks.starter.web.GlobalResultHandler;
import com.lcsk42.frameworks.starter.web.initialize.InitializeDispatcherServletController;
import com.lcsk42.frameworks.starter.web.initialize.InitializeDispatcherServletHandler;
import com.lcsk42.frameworks.starter.web.initialize.PortHolder;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.BaseHibernateValidatorConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.client.RestTemplate;

/**
 * Web 自动化配置类，用于设置通用的 web 相关 bean 和工具组件。
 */
@Slf4j
@PropertySource(value = "classpath:default-web.yml", factory = YamlPropertySourceFactory.class)
public class WebAutoConfiguration {

    /**
     * DispatcherServlet 初始化端点路径。
     */
    public static final String INITIALIZE_PATH = "/initialize/dispatcher-servlet";

    /**
     * 全局异常处理器，用于拦截所有控制器级别的异常。
     */
    @Bean
    @ConditionalOnMissingBean
    public GlobalExceptionHandler globalExceptionHandler() {
        return new GlobalExceptionHandler();
    }

    /**
     * 全局结果处理器，用于统一 API 响应格式。
     */
    @Bean
    @ConditionalOnMissingBean
    public GlobalResultHandler globalResultHandler() {
        return new GlobalResultHandler();
    }

    /**
     * 初始化轻量级控制器，用于提前触发 DispatcherServlet。
     */
    @Bean
    public InitializeDispatcherServletController initializeDispatcherServletController() {
        return new InitializeDispatcherServletController();
    }

    /**
     * PortHolder bean 用于持有 web 服务器端口号。 用于提前初始化 DispatcherServlet。
     */
    @Bean
    public PortHolder portHolder() {
        return new PortHolder();
    }

    /**
     * 带有自定义 HTTP 客户端工厂的 RestTemplate bean。
     */
    @Bean
    public RestTemplate simpleRestTemplate(ClientHttpRequestFactory factory) {
        return new RestTemplate(factory);
    }

    /**
     * 具备超时设置的基础 ClientHttpRequestFactory。 提高容错能力和响应能力。
     */
    @Bean
    public ClientHttpRequestFactory simpleClientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setReadTimeout(5 * 1_000);
        factory.setConnectTimeout(5 * 1_000);
        return factory;
    }

    /**
     * CommandLineRunner bean，用于在 Spring Boot 启动后立即调用 DispatcherServlet 初始化端点， 减少首次响应延迟。
     */
    @Bean
    public InitializeDispatcherServletHandler initializeDispatcherServletHandler(
            RestTemplate simpleRestTemplate,
            PortHolder portHolder, ConfigurableEnvironment configurableEnvironment) {
        return new InitializeDispatcherServletHandler(simpleRestTemplate, portHolder,
                configurableEnvironment);
    }

    /**
     * 基础 WebMvcConfigurer 实现，为未来扩展预留（如 CORS、格式化器、拦截器等）。
     */
    @Bean
    public WebConfiguration webConfig() {
        return new WebConfiguration();
    }

    /**
     * Validator 失败立即返回模式配置
     *
     * <p>
     * 默认情况下会校验完所有字段，然后才抛出异常。
     * </p>
     */
    @Bean
    public Validator validator(MessageSource messageSource) {
        try (LocalValidatorFactoryBean factoryBean = new LocalValidatorFactoryBean()) {
            // 国际化
            factoryBean.setValidationMessageSource(messageSource);
            // 快速失败
            factoryBean.getValidationPropertyMap()
                    .put(BaseHibernateValidatorConfiguration.FAIL_FAST, Boolean.TRUE.toString());
            factoryBean.afterPropertiesSet();
            return factoryBean.getValidator();
        }
    }

    @PostConstruct
    public void postConstruct() {
        log.debug("[Omega] - Auto Configuration 'Web' completed initialization.");
    }
}
