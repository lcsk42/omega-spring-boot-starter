package com.lcsk42.frameworks.starter.crypto.core.processor.impl;

import com.lcsk42.frameworks.starter.crypto.core.context.CryptoContext;
import com.lcsk42.frameworks.starter.crypto.core.processor.AbstractCryptoProcessor;
import com.lcsk42.frameworks.starter.crypto.core.util.CryptoUtil;

public class Base64Processor extends AbstractCryptoProcessor {
    protected Base64Processor(CryptoContext context) {
        super(context);
    }

    @Override
    public String encrypt(String plaintext) {
        return CryptoUtil.encodeBase64(plaintext);
    }

    @Override
    public String decrypt(String ciphertext) {
        return CryptoUtil.decodeBase64(ciphertext);
    }
}
