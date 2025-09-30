package com.lcsk42.frameworks.starter.idempotent.impl.token;

import com.lcsk42.frameworks.starter.idempotent.handler.IdempotentExecuteHandler;

/**
 * Token 实现幂等接口
 */
public interface IdempotentTokenService extends IdempotentExecuteHandler {

    /**
     * 创建幂等验证Token
     */
    String createToken(Long expiredMillis);
}
