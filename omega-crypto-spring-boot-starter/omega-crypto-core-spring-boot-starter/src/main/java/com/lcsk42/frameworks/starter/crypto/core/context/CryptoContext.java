package com.lcsk42.frameworks.starter.crypto.core.context;

import com.lcsk42.frameworks.starter.crypto.core.enums.Algorithm;
import com.lcsk42.frameworks.starter.crypto.core.processor.CryptoProcessor;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CryptoContext {
    /**
     * 默认算法
     */
    private Algorithm algorithm;

    /**
     * 加密/解密处理器
     * <p>
     * 优先级高于加密/解密算法
     * </p>
     */
    Class<? extends CryptoProcessor> processor;

    /**
     * 对称加密算法密钥
     */
    private String password;

    /**
     * 非对称加密算法公钥
     */
    private String publicKey;

    /**
     * 非对称加密算法私钥
     */
    private String privateKey;
}
