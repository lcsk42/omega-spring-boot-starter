package com.lcsk42.frameworks.starter.web.feign;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.RequestInterceptor;
import feign.Retryer;
import feign.codec.Decoder;
import feign.optionals.OptionalDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.cloud.openfeign.support.HttpMessageConverterCustomizer;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.DispatcherServlet;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class FeignSupportConfig {

    // 用于创建 HttpMessageConverters 的工厂
    private final ObjectFactory<HttpMessageConverters> messageConverters;

    /**
     * 配置 Feign 解码器以处理来自 Feign 客户端的响应。
     * 它组合了多个解码器，包括对响应的可选支持。
     */
    @Bean
    public Decoder feignDecoder(ObjectProvider<HttpMessageConverterCustomizer> customizers,
            ObjectMapper objectMapper) {
        return new OptionalDecoder(
                new ResponseEntityDecoder(
                        new FeignResultDecoder(
                                new SpringDecoder(this.messageConverters, customizers),
                                objectMapper)));
    }

    /**
     * 配置一个支持 "text/html" 和 "text/plain" 内容类型的 RestTemplate bean。
     * 这是必要的，因为 RestTemplate 默认不支持 "text/html;charset=UTF-8"。
     *
     * @return 带有自定义消息转换器的 RestTemplate 实例
     */
    @Bean
    public RestTemplate restTemplate() {
        final RestTemplate restTemplate = new RestTemplate();

        // 创建一个用于处理 JSON 和基于文本的内容类型的消息转换器
        final MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter =
                new MappingJackson2HttpMessageConverter();


        // 添加对 "text/html" 和 "text/plain" 内容类型的支持
        mappingJackson2HttpMessageConverter.setSupportedMediaTypes(
                List.of(MediaType.TEXT_HTML, MediaType.TEXT_PLAIN));

        restTemplate.getMessageConverters().add(mappingJackson2HttpMessageConverter);
        return restTemplate;
    }

    /**
     * 配置 Feign 客户端在请求失败时永不重试。
     * 这有助于防止在失败时自动重试。
     *
     * @return 配置为永不重试的 Feign retryer
     */
    @Bean
    public Retryer feignRetryer() {
        return Retryer.NEVER_RETRY;
    }

    /**
     * 向 Spring 应用上下文中注册 DispatcherServlet。
     * 这对于处理传入的 HTTP 请求是必要的。
     *
     * @param servlet 要注册的 DispatcherServlet 实例
     * @return DispatcherServlet 的 ServletRegistrationBean
     */
    @Bean
    public ServletRegistrationBean<DispatcherServlet> dispatcherRegistration(
            DispatcherServlet servlet) {
        servlet.setThreadContextInheritable(true);
        return new ServletRegistrationBean<>(servlet, "/**");
    }

    /**
     * 为 Feign 客户端配置 RequestInterceptor，用于将 HTTP 头信息
     * 从传入请求转发到传出的 Feign 请求。
     * 这对于传递认证令牌或其他头信息非常有用。
     *
     * @return 复制当前请求头信息的 RequestInterceptor
     */
    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> Optional.ofNullable(RequestContextHolder.getRequestAttributes())
                .filter(ServletRequestAttributes.class::isInstance)
                .map(ServletRequestAttributes.class::cast)
                .map(ServletRequestAttributes::getRequest)
                .ifPresent(request -> {
                    Collections.list(request.getHeaderNames()).stream()
                            .filter(name -> StringUtils.equalsIgnoreCase(HttpHeaders.CONTENT_LENGTH,
                                    name))
                            .forEach(name -> template.header(name, request.getHeader(name)));
                });
    }
}
