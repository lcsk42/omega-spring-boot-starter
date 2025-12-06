package com.lcsk42.frameworks.starter.web.filter;

import com.lcsk42.frameworks.starter.common.util.JwtUtil;
import com.lcsk42.frameworks.starter.common.util.UserContext;
import com.lcsk42.frameworks.starter.convention.model.BaseUserInfoDTO;
import com.lcsk42.frameworks.starter.web.service.TokenService;
import com.lcsk42.frameworks.starter.web.util.ServletUtil;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;

import java.io.IOException;

@RequiredArgsConstructor
public class UserTransmitFilter implements Filter {

    private final TokenService tokenService;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
            FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String authorization = ServletUtil.getHeader(httpServletRequest, HttpHeaders.AUTHORIZATION);
        if (StringUtils.isNoneBlank(authorization)
                && StringUtils.startsWith(authorization, JwtUtil.TOKEN_PREFIX)) {
            BaseUserInfoDTO baseUserInfoDTO = tokenService.handlerToken(authorization);
            UserContext.setUser(baseUserInfoDTO);
        }
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            UserContext.removeUser();
        }
    }
}
