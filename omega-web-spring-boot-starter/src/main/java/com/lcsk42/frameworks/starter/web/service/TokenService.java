package com.lcsk42.frameworks.starter.web.service;

import com.lcsk42.frameworks.starter.convention.model.BaseUserInfoDTO;

public interface TokenService {
    BaseUserInfoDTO handlerToken(String token);
}
