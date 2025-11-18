package com.lcsk42.frameworks.starter.crypto.core.processor.impl;

import com.lcsk42.frameworks.starter.crypto.core.context.CryptoContext;
import com.lcsk42.frameworks.starter.crypto.core.processor.AbstractCryptoProcessor;
import com.lcsk42.frameworks.starter.crypto.core.util.CryptoUtil;

public class AesProcessor extends AbstractCryptoProcessor {

    protected AesProcessor(CryptoContext context) {
        super(context);
    }

    @Override
    public String encrypt(String plaintext) {
        return CryptoUtil.encryptAes(plaintext, context.getPassword());
    }

    @Override
    public String decrypt(String ciphertext) {
        return CryptoUtil.decryptAes(ciphertext, context.getPassword());
    }
}
