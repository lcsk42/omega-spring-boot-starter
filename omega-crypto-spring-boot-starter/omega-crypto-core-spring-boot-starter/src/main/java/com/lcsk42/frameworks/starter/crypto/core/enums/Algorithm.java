package com.lcsk42.frameworks.starter.crypto.core.enums;

import com.lcsk42.frameworks.starter.crypto.core.processor.CryptoProcessor;
import com.lcsk42.frameworks.starter.crypto.core.processor.impl.AesProcessor;
import com.lcsk42.frameworks.starter.crypto.core.processor.impl.Base64Processor;
import com.lcsk42.frameworks.starter.crypto.core.processor.impl.RsaProcessor;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Algorithm {

    /**
     * 默认使用配置属性的算法
     */
    DEFAULT(null),

    /**
     * AES
     */
    AES(AesProcessor.class),

    /**
     * RSA
     */
    RSA(RsaProcessor.class),

    /**
     * Base64
     */
    BASE64(Base64Processor.class),
    ;

    private final Class<? extends CryptoProcessor> cryptoProcessor;
}
