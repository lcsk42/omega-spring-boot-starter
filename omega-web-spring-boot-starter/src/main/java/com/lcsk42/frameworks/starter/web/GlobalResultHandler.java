package com.lcsk42.frameworks.starter.web;

import com.lcsk42.frameworks.starter.convention.model.Result;
import com.lcsk42.frameworks.starter.core.constant.HttpHeaderConstant;
import com.lcsk42.frameworks.starter.json.jackson.util.JacksonUtil;
import com.lcsk42.frameworks.starter.web.annotation.CompatibleOutput;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.PathContainer;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@RestControllerAdvice
public class GlobalResultHandler implements ResponseBodyAdvice<Object> {

    /**
     * 判断是否应处理响应体。此方法检查：
     * 1. 检查类是否具有 @RestController 注解, 如果没有，不处理
     * 2. 检查类或方法是否具有 @CompatibleOutput 注解, 如果有, 则不处理
     * 3. 检查返回值是否为 Result 类型
     */
    @Override
    public boolean supports(@NonNull MethodParameter returnType,
            @NonNull Class<? extends HttpMessageConverter<?>> converterType) {


        // 判断是否是 swagger 的请求
        String uri = Optional.ofNullable(RequestContextHolder.getRequestAttributes())
                .map(requestAttributes -> (ServletRequestAttributes) requestAttributes)
                .map(ServletRequestAttributes::getRequest)
                .map(HttpServletRequest::getRequestURI)
                .orElse(StringUtils.EMPTY);

        if (Stream.of(
                "/doc/**",
                "/v2/api-docs/**",
                "/v3/api-docs/**",
                "/webjars/**",
                "/swagger-resources/**",
                "/swagger-ui.html").anyMatch(resourcePath -> isMatch(uri, resourcePath))) {
            return false;
        }

        Class<?> controllerClass = returnType.getContainingClass();

        // 如果没有 RestController 注解，不处理
        boolean hasRestController =
                AnnotationUtils.findAnnotation(controllerClass, RestController.class) != null;
        if (!hasRestController) {
            return false;
        }

        // 如果类上有 @CompatibleOutput 注解，不处理
        boolean hasClassAnnotation =
                AnnotationUtils.findAnnotation(controllerClass, CompatibleOutput.class) != null;
        if (hasClassAnnotation) {
            return false;
        }

        // 如果方法上有 @CompatibleOutput 注解，不处理
        boolean hasMethodAnnotation =
                returnType.getMethodAnnotation(CompatibleOutput.class) != null;
        if (hasMethodAnnotation) {
            return false;
        }

        // 如果返回类型已经比 R.class，不处理
        boolean isResultType = returnType.getParameterType().equals(Result.class);
        if (isResultType) {
            return false;
        }

        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null;
    }

    /**
     * 在响应体写入响应前对其进行修改。 1. 若响应体不是字符串类型，则将其包装为 R 对象 2. 若为字符串类型，则转换为 JSON 格式的 R 对象
     */
    @Override
    public Object beforeBodyWrite(Object body, @NonNull MethodParameter returnType,
            @NonNull MediaType selectedContentType,
            @NonNull Class<? extends HttpMessageConverter<?>> selectedConverterType,
            @NonNull ServerHttpRequest request,
            @NonNull ServerHttpResponse response) {

        String requestId = request.getHeaders().getFirst(HttpHeaderConstant.REQUEST_ID);

        if (StringUtils.isBlank(requestId)) {
            // 如果 request ID 是空的，生成一个新的
            requestId = HttpHeaderConstant.getRequestId();
        }

        if (Objects.isNull(body)) {
            return Result.ok().withRequestId(requestId);
        }

        // 若返回类型是字符串，将其转换为 JSON 格式的 Result 对象
        if (returnType.getParameterType().isAssignableFrom(String.class)) {
            String json = JacksonUtil.toJSON(Result.ok(body).withRequestId(requestId));
            // 设置响应内容类型为 application/json
            // 由于 returnType.getParameterType() 是 String 类型，默认内容类型会是 text/plain
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            return ObjectUtils.defaultIfNull(json, body.toString());
        }

        // 其他情况下，直接将响应体包装为 Result 对象
        return Result.ok(body).withRequestId(requestId);
    }

    private boolean isMatch(String path, String pattern) {
        PathPattern pathPattern = PathPatternParser.defaultInstance.parse(pattern);
        PathContainer pathContainer = PathContainer.parsePath(path);
        return pathPattern.matches(pathContainer);
    }
}
