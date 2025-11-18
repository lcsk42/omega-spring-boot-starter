package com.lcsk42.frameworks.starter.crypto.core.util;


import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CryptoUtilTest {

    public static final String TEXT = "hello world";
    public static final String BASE64_ENCODED_TEXT = "aGVsbG8gd29ybGQ=";

    @Test
    void encodeBase64() {
        String encodeBase64 = CryptoUtil.encodeBase64(TEXT);
        assertThat(encodeBase64).isEqualTo(BASE64_ENCODED_TEXT);
    }

    @Test
    void decodeBase64() {
        String decodeBase64 = CryptoUtil.decodeBase64(BASE64_ENCODED_TEXT);
        assertThat(decodeBase64).isEqualTo(TEXT);
    }

    @Test
    void aes() {
        String aesKey = CryptoUtil.generateAesKey();
        assertThat(aesKey)
                .isNotBlank();
        String iv = CryptoUtil.generateIVString();
        String encryptAes = CryptoUtil.encryptAes(TEXT, aesKey, iv);
        assertThat(encryptAes)
                .isNotBlank();
        String decryptAes = CryptoUtil.decryptAes(encryptAes, aesKey, iv);
        assertThat(decryptAes)
                .isNotBlank()
                .isEqualTo(TEXT);
    }

    @Test
    void rsa() {
        Pair<String, String> pair = CryptoUtil.generateRsaKeyPair();
        String publicKey = pair.getLeft();
        String privateKey = pair.getRight();
        assertThat(publicKey).isNotBlank();
        assertThat(privateKey).isNotBlank();
        String encryptRsa = CryptoUtil.encryptRsa(TEXT, publicKey);
        assertThat(encryptRsa)
                .isNotBlank();
        String decryptRsa = CryptoUtil.decryptRsa(encryptRsa, privateKey);
        assertThat(decryptRsa)
                .isNotBlank()
                .isEqualTo(TEXT);

    }
}
