package com.lcsk42.frameworks.starter.crypto.core.processor;

/**
 * 加解接口
 */
public interface CryptoProcessor {

    /**
     * 加密
     *
     * @param plaintext 明文
     * @return 加密后的文本
     */
    String encrypt(String plaintext);

    /**
     * 解密
     *
     * @param ciphertext 密文
     * @return 解密后的文本
     */
    String decrypt(String ciphertext);
}
