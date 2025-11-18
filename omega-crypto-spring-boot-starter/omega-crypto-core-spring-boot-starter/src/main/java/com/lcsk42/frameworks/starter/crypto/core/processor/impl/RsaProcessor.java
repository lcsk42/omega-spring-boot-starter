package com.lcsk42.frameworks.starter.crypto.core.processor.impl;

import com.lcsk42.frameworks.starter.crypto.core.context.CryptoContext;
import com.lcsk42.frameworks.starter.crypto.core.processor.AbstractCryptoProcessor;
import com.lcsk42.frameworks.starter.crypto.core.util.CryptoUtil;

public class RsaProcessor extends AbstractCryptoProcessor {

    protected RsaProcessor(CryptoContext context) {
        super(context);
    }

    @Override
    public String encrypt(String plaintext) {
        return CryptoUtil.encryptRsa(plaintext, context.getPublicKey());
    }

    @Override
    public String decrypt(String ciphertext) {
        return CryptoUtil.encryptRsa(ciphertext, context.getPrivateKey());
    }
}
