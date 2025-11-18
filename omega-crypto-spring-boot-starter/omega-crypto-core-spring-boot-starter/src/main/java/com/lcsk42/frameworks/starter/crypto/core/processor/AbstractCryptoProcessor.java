package com.lcsk42.frameworks.starter.crypto.core.processor;

import com.lcsk42.frameworks.starter.crypto.core.context.CryptoContext;

/**
 * 加密器基类
 */
public abstract class AbstractCryptoProcessor implements CryptoProcessor {

    protected final CryptoContext context;

    protected AbstractCryptoProcessor(CryptoContext context) {
        // 配置校验与配置注入
        this.context = context;
    }
}