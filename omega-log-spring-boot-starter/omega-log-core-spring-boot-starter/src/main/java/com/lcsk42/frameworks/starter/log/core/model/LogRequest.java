package com.lcsk42.frameworks.starter.log.core.model;

import com.lcsk42.frameworks.starter.common.util.IpUtil;
import com.lcsk42.frameworks.starter.json.jackson.util.JacksonUtil;
import com.lcsk42.frameworks.starter.log.core.enums.Include;
import com.lcsk42.frameworks.starter.log.core.exception.LogErrorCode;
import com.lcsk42.frameworks.starter.log.core.util.LogUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import ua_parser.Client;
import ua_parser.Parser;

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
    private final String method;

    /**
     * 请求 URL
     */
    private final URI url;

    /**
     * IP
     */
    private final String ip;

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
        HttpServletRequest request =
                Optional.ofNullable(RequestContextHolder.getRequestAttributes())
                        .map(attributes -> (ServletRequestAttributes) attributes)
                        .map(ServletRequestAttributes::getRequest)
                        .orElseThrow(LogErrorCode.MISSING_REQUEST::toServiceException);

        // 请求方法
        this.method = LogUtil.getRequestMethod();

        // 请求的 url
        this.url = LogUtil.getRequestUrl();

        // ip
        this.ip = LogUtil.getRequestIp();

        // 请求头
        if (LogUtil.contains(includes, Include.REQUEST_HEADERS)) {
            this.headers = LogUtil.getRequestHeaders();
        }

        // 请求体
        if (LogUtil.contains(includes, Include.REQUEST_BODY)) {
            this.body = LogUtil.getRequestBody();
        }

        // 请求参数
        if (LogUtil.contains(includes, Include.REQUEST_PARAM)) {
            this.param = LogUtil.getRequestParams();
        }

        // 实际地址
        if (LogUtil.contains(includes, Include.IP_ADDRESS)) {
            this.address = IpUtil.getIpv4Address(this.ip);
        }

        if (this.headers == null) {
            return;
        }

        if (LogUtil.contains(includes, Include.BROWSER, Include.OS)) {
            String userAgentString = this.headers.entrySet()
                    .stream()
                    .filter(h -> HttpHeaders.USER_AGENT.equalsIgnoreCase(h.getKey()))
                    .map(Map.Entry::getValue)
                    .findFirst()
                    .orElse(null);
            if (StringUtils.isBlank(userAgentString)) {
                return;
            }

            Client client = new Parser().parse(userAgentString);

            // 浏览器
            if (LogUtil.contains(includes, Include.BROWSER)) {
                this.browser = client.userAgent.family + " " + client.userAgent.major;
            }

            // 操作系统
            if (LogUtil.contains(includes, Include.OS)) {
                this.os = client.os.family + " " + client.os.major;
            }
        }
    }

    @Override
    public String toString() {
        return JacksonUtil.toPrettyJson(this);
    }
}
