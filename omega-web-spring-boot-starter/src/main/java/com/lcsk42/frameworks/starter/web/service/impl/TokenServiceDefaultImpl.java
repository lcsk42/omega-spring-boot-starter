package com.lcsk42.frameworks.starter.web.service.impl;

import com.lcsk42.frameworks.starter.common.util.JwtUtil;
import com.lcsk42.frameworks.starter.convention.model.BaseUserInfoDTO;
import com.lcsk42.frameworks.starter.web.service.TokenService;
import org.apache.commons.collections4.MapUtils;


public class TokenServiceDefaultImpl implements TokenService {
    private final String secret = System.getenv("FRAMEWORK_GATEWAY_TOKEN_SECRET");

    @Override
    public BaseUserInfoDTO handlerToken(String token) {

        return JwtUtil.parseToken(token, secret)
                .map(claims -> BaseUserInfoDTO.of(
                        MapUtils.getLong(claims, "id"),
                        MapUtils.getString(claims, "username"),
                        token))
                .orElseGet(BaseUserInfoDTO::empty);
    }
}
