package com.lcsk42.frameworks.starter.security.xss.filter;

import com.lcsk42.frameworks.starter.security.xss.configuration.XssProperties;
import com.lcsk42.frameworks.starter.web.util.WebUtil;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class XssFilter implements Filter {

    private final XssProperties xssProperties;

    @Override
    public void init(FilterConfig filterConfig) {
        log.debug("[Omega] - Auto Configuration 'Web-XssFilter' completed initialization.");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
            FilterChain filterChain)
            throws IOException, ServletException {

        // 未开启 XSS 过滤，则直接跳过
        if (servletRequest instanceof HttpServletRequest request && xssProperties.isEnabled()) {
            // 放行路由：忽略 XSS 过滤
            List<String> excludePatterns = xssProperties.getExcludePatterns();
            if (CollectionUtils.isNotEmpty(excludePatterns)
                    && WebUtil.isMatch(request.getServletPath(), excludePatterns)) {
                filterChain.doFilter(request, servletResponse);
                return;
            }
            // 拦截路由：执行 XSS 过滤
            List<String> includePatterns = xssProperties.getIncludePatterns();
            if (CollectionUtils.isNotEmpty(includePatterns)) {
                if (WebUtil.isMatch(request.getServletPath(), includePatterns)) {
                    filterChain.doFilter(new XssServletRequestWrapper(request, xssProperties),
                            servletResponse);
                } else {
                    filterChain.doFilter(request, servletResponse);
                }
                return;
            }
            // 默认：执行 XSS 过滤
            filterChain.doFilter(new XssServletRequestWrapper(request, xssProperties),
                    servletResponse);
            return;
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
