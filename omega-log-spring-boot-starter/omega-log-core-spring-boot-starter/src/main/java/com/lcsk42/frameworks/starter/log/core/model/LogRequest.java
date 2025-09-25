package com.lcsk42.frameworks.starter.log.core.model;

import com.lcsk42.frameworks.starter.common.util.IpUtil;
import com.lcsk42.frameworks.starter.log.core.enums.Include;
import com.lcsk42.frameworks.starter.log.core.exception.LogErrorCode;
import com.lcsk42.frameworks.starter.log.core.util.AccessLogUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import nl.basjes.parse.useragent.UserAgent;
import nl.basjes.parse.useragent.UserAgentAnalyzer;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.net.URI;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * 请求信息
 */
@Getter
public class LogRequest {
    /**
     * 请求方式
     */
    private String method;

    /**
     * 请求 URL
     */
    private URI url;

    /**
     * IP
     */
    private String ip;

    /**
     * 请求头
     */
    private Map<String, String> headers;

    /**
     * 请求体（JSON 字符串）
     */
    private String body;

    /**
     * 请求参数
     */
    private Map<String, Object> param;

    /**
     * IP 归属地
     */
    private String address;

    /**
     * 浏览器
     */
    private String browser;

    /**
     * 操作系统
     */
    private String os;

    public LogRequest(Set<Include> includes) {
        HttpServletRequest request = Optional.ofNullable(RequestContextHolder.getRequestAttributes())
                .map(attributes -> (ServletRequestAttributes) attributes)
                .map(ServletRequestAttributes::getRequest)
                .orElseThrow(LogErrorCode.MISSING_REQUEST::toServiceException);

        // 请求方法
        this.method = AccessLogUtil.getRequestMethod();

        // 请求的 url
        this.url = AccessLogUtil.getRequestUrl();

        // ip
        this.ip = AccessLogUtil.getRequestIp();

        if (includes.contains(Include.REQUEST_HEADERS)) {
            this.headers = AccessLogUtil.getRequestHeaders();
        }

        // 请求体
        if (includes.contains(Include.REQUEST_BODY)) {
            this.body = AccessLogUtil.getRequestBody();
        }

        // 请求参数
        if (includes.contains(Include.REQUEST_PARAM)) {
            this.param = AccessLogUtil.getRequestParams();
        }

        // 实际地址
        if (includes.contains(Include.IP_ADDRESS)) {
            this.address = IpUtil.getIpv4Address(this.ip);
        }

        if (this.headers == null) {
            return;
        }

        String userAgentString = this.headers.entrySet()
                .stream()
                .filter(h -> HttpHeaders.USER_AGENT.equalsIgnoreCase(h.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);
        if (StringUtils.isBlank(userAgentString)) {
            return;
        }

        UserAgent.ImmutableUserAgent userAgent = UserAgentAnalyzer.newBuilder()
                .build()
                .parse(userAgentString);

        // 浏览器
        if (includes.contains(Include.BROWSER)) {
            this.browser = userAgent.getValue(UserAgent.AGENT_NAME_VERSION);
        }

        // 操作系统
        if (includes.contains(Include.OS)) {
            this.os = userAgent.getValue(UserAgent.OPERATING_SYSTEM_NAME_VERSION);
        }
    }
}
