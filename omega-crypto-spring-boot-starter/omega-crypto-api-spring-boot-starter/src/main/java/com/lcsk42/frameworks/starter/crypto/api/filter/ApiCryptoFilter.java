package com.lcsk42.frameworks.starter.crypto.api.filter;

import com.lcsk42.frameworks.starter.crypto.api.annotation.ApiEncrypt;
import com.lcsk42.frameworks.starter.crypto.api.config.ApiCryptoProperties;
import com.lcsk42.frameworks.starter.web.util.WebUtil;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;

import java.io.IOException;
import java.util.Optional;

/**
 * API 加密过滤器
 */
public class ApiCryptoFilter implements Filter {

    private final ApiCryptoProperties properties;

    public ApiCryptoFilter(ApiCryptoProperties properties) {
        this.properties = properties;
    }

    @Override
    public void doFilter(ServletRequest servletRequest,
            ServletResponse servletResponse,
            FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        // 是否加密响应
        boolean isResponseEncrypt = this.isResponseEncrypt(request);
        // 密钥标头
        String secretKeyHeader = properties.getSecretKeyHeader();
        ServletRequest requestWrapper = null;
        ServletResponse responseWrapper = null;
        ResponseBodyEncryptWrapper responseBodyEncryptWrapper = null;
        // 是否为 PUT 或者 POST 请求
        if (HttpMethod.PUT.matches(request.getMethod())
                || HttpMethod.POST.matches(request.getMethod())) {
            // 获取密钥值
            String secretKeyValue = request.getHeader(secretKeyHeader);
            if (StringUtils.isNotBlank(secretKeyValue)) {
                // 请求解密
                requestWrapper = new RequestBodyDecryptWrapper(request, properties.getPrivateKey(),
                        secretKeyHeader);
            }
        }
        // 响应加密，响应包装器替换响应体加密包装器
        if (isResponseEncrypt) {
            responseBodyEncryptWrapper = new ResponseBodyEncryptWrapper(response);
            responseWrapper = responseBodyEncryptWrapper;
        }
        // 继续执行
        chain.doFilter(ObjectUtils.defaultIfNull(requestWrapper, request), ObjectUtils
                .defaultIfNull(responseWrapper, response));
        // 响应加密，执行完成后，响应密文
        if (isResponseEncrypt) {
            servletResponse.reset();
            // 获取密文
            String encryptContent =
                    responseBodyEncryptWrapper.getEncryptContent(response, properties
                            .getPublicKey(), secretKeyHeader);
            // 写出密文
            servletResponse.getWriter().write(encryptContent);
        }
    }

    /**
     * 是否加密响应
     *
     * @param request 请求对象
     * @return 是否加密响应
     */
    private boolean isResponseEncrypt(HttpServletRequest request) {
        // 获取 API 加密注解
        ApiEncrypt apiEncrypt = Optional.ofNullable(WebUtil.getHandlerMethod(request))
                .map(h -> h.getMethodAnnotation(ApiEncrypt.class))
                .orElse(null);
        return apiEncrypt != null && apiEncrypt.response();
    }
}
