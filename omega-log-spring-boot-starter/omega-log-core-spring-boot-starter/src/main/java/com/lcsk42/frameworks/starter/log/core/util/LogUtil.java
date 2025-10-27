package com.lcsk42.frameworks.starter.log.core.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.lcsk42.frameworks.starter.common.util.IdUtil;
import com.lcsk42.frameworks.starter.common.util.net.NetworkUtil;
import com.lcsk42.frameworks.starter.core.constant.HttpHeaderConstant;
import com.lcsk42.frameworks.starter.core.constant.StringConstant;
import com.lcsk42.frameworks.starter.json.jackson.util.JacksonUtil;
import com.lcsk42.frameworks.starter.log.core.config.AccessLogProperties;
import com.lcsk42.frameworks.starter.log.core.config.LogProperties;
import com.lcsk42.frameworks.starter.log.core.enums.Include;
import com.lcsk42.frameworks.starter.log.core.exception.LogErrorCode;
import com.lcsk42.frameworks.starter.log.core.wrapper.RepeatReadRequestWrapper;
import com.lcsk42.frameworks.starter.log.core.wrapper.RepeatReadResponseWrapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.server.PathContainer;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class LogUtil {
    /**
     * 资源路径 - doc 路径
     */
    private static final List<String> RESOURCE_PATH = List.of(
            "/doc/**",
            "/v2/api-docs/**",
            "/v3/api-docs/**",
            "/webjars/**",
            "/swagger-resources/**",
            "/swagger-ui.html");

    /**
     * 获取参数信息
     *
     * @param properties 属性
     * @return {@link String }
     */
    public static String getParam(AccessLogProperties properties) {
        // 是否需要打印请求参数
        if (!properties.getPrintRequest()) {
            return null;
        }

        // 参数为空返回空
        Object params;
        try {
            params = getAccessLogReqParam();
        } catch (Exception e) {
            return null;
        }
        if (ObjectUtils.isEmpty(params)) {
            return null;
        }

        // 是否需要对特定入参脱敏
        if (properties.getFilterSensitive()) {
            params = processSensitiveParams(params, properties.getSensitiveFields());
        }

        // 是否自动截断超长参数值
        if (properties.getTruncate()) {
            params = processTruncateLongParams(
                    params,
                    properties.getThreshold(),
                    properties.getMaxLength(),
                    properties.getLongParamSuffix());
        }
        return JacksonUtil.toJSON(params);
    }

    /**
     * 排除路径
     *
     * @param properties 属性
     * @param path 路径
     * @return boolean
     */
    public static boolean exclusionPath(LogProperties properties, String path) {
        // 放行路由配置的排除检查
        return Stream.of(properties.getExcludePatterns(), RESOURCE_PATH)
                .flatMap(List::stream)
                .anyMatch(resourcePath -> isMatch(path, resourcePath));
    }

    /**
     * 处理敏感参数，支持 Map 和 List<Map<String, Object>> 类型
     *
     * @param params 参数
     * @param sensitiveParams 敏感参数列表
     * @return 处理后的参数
     */
    @SuppressWarnings("unchecked")
    private static Object processSensitiveParams(Object params, List<String> sensitiveParams) {
        if (params instanceof Map) {
            return filterSensitiveParams((Map<String, Object>) params, sensitiveParams);
        } else if (params instanceof List) {
            return ((List<?>) params).stream()
                    .filter(item -> item instanceof Map)
                    .map(item -> filterSensitiveParams((Map<String, Object>) item, sensitiveParams))
                    .collect(Collectors.toList());
        }
        return params;
    }

    /**
     * 过滤敏感参数
     *
     * @param params 参数 Map
     * @param sensitiveParams 敏感参数列表
     * @return 处理后的参数 Map
     */
    private static Map<String, Object> filterSensitiveParams(Map<String, Object> params,
            List<String> sensitiveParams) {
        if (params == null || params.isEmpty() || sensitiveParams == null
                || sensitiveParams.isEmpty()) {
            return params;
        }

        Map<String, Object> filteredParams = new HashMap<>(params);
        for (String sensitiveKey : sensitiveParams) {
            if (filteredParams.containsKey(sensitiveKey)) {
                filteredParams.put(sensitiveKey, "***");
            }
        }
        return filteredParams;
    }

    /**
     * 处理超长参数，支持 Map 和 List<Map<String, Object>> 类型
     *
     * @param params 参数
     * @param threshold 截断阈值（值长度超过该值才截断）
     * @param maxLength 最大长度
     * @param suffix 后缀（如 "..."）
     * @return 处理后的参数
     */
    @SuppressWarnings("unchecked")
    private static Object processTruncateLongParams(Object params, int threshold, int maxLength,
            String suffix) {
        if (params instanceof Map) {
            return truncateLongParams((Map<String, Object>) params, threshold, maxLength, suffix);
        } else if (params instanceof List) {
            return ((List<?>) params).stream()
                    .filter(Map.class::isInstance)
                    .map(item -> truncateLongParams((Map<String, Object>) item, threshold,
                            maxLength, suffix))
                    .collect(Collectors.toList());
        }
        return params;
    }

    /**
     * 截断超长参数
     *
     * @param params 参数 Map
     * @param threshold 截断阈值（值长度超过该值才截断）
     * @param maxLength 最大长度
     * @param suffix 后缀（如 "..."）
     * @return 处理后的参数 Map
     */
    private static Map<String, Object> truncateLongParams(Map<String, Object> params,
            int threshold,
            int maxLength,
            String suffix) {
        if (params == null || params.isEmpty()) {
            return params;
        }

        Map<String, Object> truncatedParams = new HashMap<>(params);
        for (Map.Entry<String, Object> entry : truncatedParams.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof String strValue) {
                if (strValue.length() > threshold) {
                    entry.setValue(
                            strValue.substring(0, Math.min(strValue.length(), maxLength)) + suffix);
                }
            }
        }
        return truncatedParams;
    }

    /**
     * 获取访问日志请求参数
     *
     * @return {@link Object }
     */
    private static Object getAccessLogReqParam() {
        HttpServletRequest request =
                Optional.ofNullable(RequestContextHolder.getRequestAttributes())
                        .map(attributes -> (ServletRequestAttributes) attributes)
                        .map(ServletRequestAttributes::getRequest)
                        .orElseThrow(LogErrorCode.MISSING_REQUEST::toServiceException);

        String body = null;
        if (!(request instanceof RepeatReadRequestWrapper wrapper)
                || !wrapper.isMultipartContent(request)) {
            try (final BufferedReader reader = request.getReader()) {
                body = IOUtils.toString(reader);
            } catch (IOException ignore) {
                throw LogErrorCode.READ_REQUEST_BODY_FAILED.toServiceException();
            }
        }

        if (StringUtils.isNotBlank(body) && JacksonUtil.isJson(body)) {
            try {
                if (JacksonUtil.isJsonArray(body)) {
                    return JacksonUtil.toBean(body, List.class);
                } else {
                    return JacksonUtil.toBean(body, Map.class);
                }
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    /**
     * 路径是否匹配
     *
     * @param path 路径
     * @param pattern 匹配模式
     * @return 是否匹配
     */
    public static boolean isMatch(String path, String pattern) {
        PathPattern pathPattern = PathPatternParser.defaultInstance.parse(pattern);
        PathContainer pathContainer = PathContainer.parsePath(path);
        return pathPattern.matches(pathContainer);
    }

    /**
     * 获取请求属性
     *
     * @return {@link ServletRequestAttributes }
     */
    public static ServletRequestAttributes getRequestAttributes() {
        try {
            RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
            return (ServletRequestAttributes) attributes;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取 HTTP Request
     *
     * @return HttpServletRequest
     */
    public static HttpServletRequest getRequest() {
        ServletRequestAttributes attributes = getRequestAttributes();
        if (attributes == null) {
            return null;
        }
        return attributes.getRequest();
    }

    /**
     * 获取请求方法
     *
     * @return {@link String }
     */
    public static String getRequestMethod() {
        HttpServletRequest request = getRequest();
        return request != null ? request.getMethod() : null;
    }

    /**
     * 获取请求路径
     *
     * @return {@link URI }
     */
    public static String getRequestPath() {
        HttpServletRequest request = getRequest();
        return request != null ? request.getRequestURI() : null;
    }

    /**
     * 获取请求 URL（包含 query 参数）
     * <p>
     * {@code http://localhost:8000/system/user?page=1&size=10}
     * </p>
     *
     * @return {@link URI }
     */
    public static URI getRequestUrl() {
        HttpServletRequest request = getRequest();
        if (request == null) {
            return null;
        }
        String queryString = request.getQueryString();
        if (StringUtils.isBlank(queryString)) {
            return URI.create(request.getRequestURL().toString());
        }
        try {
            String urlBuilder = request.getRequestURL() +
                    StringConstant.QUESTION_MARK +
                    queryString;
            return new URI(urlBuilder);
        } catch (URISyntaxException e) {
            String encoded = UriUtils.encodeQuery(queryString, StandardCharsets.UTF_8);
            String urlBuilder = request.getRequestURL() +
                    StringConstant.QUESTION_MARK +
                    encoded;
            return URI.create(urlBuilder);
        }
    }

    /**
     * 获取请求 Ip
     *
     * @return {@link String }
     */
    public static String getRequestIp() {
        HttpServletRequest request = getRequest();

        for (String header : List.of(
                "X-Forwarded-For",
                "X-Real-IP",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                "HTTP_CLIENT_IP",
                "HTTP_X_FORWARDED_FOR")) {
            String ip = request.getHeader(header);
            if (StringUtils.isNotEmpty(ip) && !"unknown".equalsIgnoreCase(ip)) {
                if ("X-Forwarded-For".equalsIgnoreCase(header)) {
                    ip = StringUtils.split(ip, ',')[0].trim();
                }
                return NetworkUtil.getMultistageReverseProxyIp(ip);
            }
        }
        return NetworkUtil.getMultistageReverseProxyIp(request.getRemoteAddr());
    }

    /**
     * 获取请求头信息
     *
     * @return {@link Map }<{@link String }, {@link String }>
     * @since 2.11.0
     */
    public static Map<String, String> getRequestHeaders() {
        HttpServletRequest request = getRequest();
        return request != null ? Collections.list(request.getHeaderNames()).stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        request::getHeader,
                        (o, n) -> n))
                : Collections.emptyMap();
    }

    /**
     * 获取请求 body 参数
     *
     * @return {@link String }
     */
    public static String getRequestBody() {
        HttpServletRequest request = getRequest();
        String body = null;
        if (request instanceof RepeatReadRequestWrapper wrapper
                && !wrapper.isMultipartContent(request)) {
            try (final BufferedReader reader = request.getReader()) {
                body = IOUtils.toString(reader);
            } catch (IOException ignore) {
                throw LogErrorCode.READ_REQUEST_BODY_FAILED.toServiceException();
            }
        }
        return body;
    }

    /**
     * 获取请求参数
     *
     * @return {@link Map }<{@link String }, {@link Object }>
     */
    public static Map<String, Object> getRequestParams() {
        String body = getRequestBody();
        if (StringUtils.isNotBlank(body) && JacksonUtil.isJson(body)) {
            return JacksonUtil.toBean(body, new TypeReference<>() {});
        }
        return null;
    }

    /**
     * 获取 HTTP Response
     *
     * @return HttpServletResponse
     */
    public static HttpServletResponse getResponse() {
        ServletRequestAttributes attributes = getRequestAttributes();
        if (attributes == null) {
            return null;
        }
        return attributes.getResponse();
    }

    /**
     * 获取响应 body 参数
     *
     * @return {@link String }
     */
    public static String getResponseBody() {
        HttpServletResponse response = getResponse();
        if (response instanceof RepeatReadResponseWrapper wrapper
                && !wrapper.isStreamingResponse()) {
            String body = wrapper.getResponseContent();
            return JacksonUtil.isJson(body) ? body : null;
        }
        return null;
    }

    /**
     * 获取响应参数
     *
     * @return {@link Map }<{@link String }, {@link Object }>
     */
    public static Map<String, Object> getResponseParams() {
        String body = getResponseBody();
        return StringUtils.isNotBlank(body) && JacksonUtil.isJson(body)
                ? JacksonUtil.toBean(body, new TypeReference<>() {})
                : null;
    }

    /**
     * 获取响应状态
     *
     * @return int
     */
    public static int getResponseStatus() {
        HttpServletResponse response = getResponse();
        return response != null ? response.getStatus() : -1;
    }

    /**
     * 获取响应所有的头（header）信息
     *
     * @return header值
     */
    public static Map<String, String> getResponseHeaders() {
        HttpServletResponse response = getResponse();
        if (response == null) {
            return Collections.emptyMap();
        }
        return response.getHeaderNames().stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        response::getHeader,
                        (o, n) -> n));
    }


    /**
     * 判断是否包含某个 模块
     *
     * @param includes 集合
     * @param include 目标
     * @return 是否符合条件
     */
    public static boolean contains(Set<Include> includes, Include... include) {
        return CollectionUtils.containsAny(includes, Include.ALL, include);
    }

    /**
     * 获取请求 ID
     *
     * @return 请求 ID
     */
    public static String getRequestId() {
        HttpServletRequest request = getRequest();
        if (request == null) {
            return null;
        }
        String requestId = request.getHeader(HttpHeaderConstant.REQUEST_ID);
        return StringUtils.defaultIfBlank(requestId, IdUtil.generateStandardUuid());
    }
}
