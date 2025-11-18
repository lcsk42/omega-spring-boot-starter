package com.lcsk42.frameworks.starter.crypto.core.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * 加解密工具类
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CryptoUtil {

    private static final int[] VALID_AES_KEY_LENGTHS = {16, 24, 32};
    private static final String AES_ALGORITHM = "AES";
    private static final String AES_TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final String RSA_ALGORITHM = "RSA";
    private static final int RSA_KEY_SIZE = 2048;
    private static final String IV_PARAMETER = "1234567890123456";
    private static final String CHARS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()";

    /**
     * Base64 编码
     *
     * @param data 待编码数据
     * @return 编码后字符串
     */
    public static String encodeBase64(String data) {
        if (StringUtils.isBlank(data)) {
            return StringUtils.EMPTY;
        }
        return Base64.getEncoder().encodeToString(data.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Base64 解码
     *
     * @param data 待解码数据
     * @return 解码后字符串
     */
    public static String decodeBase64(String data) {
        if (StringUtils.isBlank(data)) {
            return StringUtils.EMPTY;
        }
        byte[] decodedBytes = Base64.getDecoder().decode(data);
        return new String(decodedBytes, StandardCharsets.UTF_8);
    }

    /**
     * 生成随机AES密钥（默认32位）
     *
     * @return Base64编码的AES密钥字符串
     */
    public static String generateAesKey() {
        return generateAesKey(32);
    }


    /**
     * 生成AES密钥
     *
     * @param keySize 密钥长度（16, 24, or 32）
     * @return Base64编码的AES密钥字符串
     */
    public static String generateAesKey(int keySize) {
        if (!ArrayUtils.contains(VALID_AES_KEY_LENGTHS, keySize)) {
            throw new IllegalArgumentException("AES 密钥长度要求为 16位、24位、32位");
        }
        // 可用的字符集（可根据需要调整）
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(keySize);
        for (int i = 0; i < keySize; i++) {
            int index = random.nextInt(CHARS.length());
            sb.append(CHARS.charAt(index));
        }
        return sb.toString();
    }

    /**
     * 生成随机 IV 字符串
     *
     * @return IV 字符串
     */
    public static String generateIVString() {
        return generateAesKey(16);
    }

    /**
     * AES 加密
     *
     * @param data 待加密数据
     * @param password 秘钥字符串
     * @return 加密后字符串, 采用 Base64 编码
     */
    public static String encryptAes(String data, String password) {
        return encryptAes(data, password, IV_PARAMETER);
    }

    /**
     * AES 加密
     *
     * @param data 待加密数据
     * @param password 秘钥字符串
     * @param iv 偏移量
     * @return 加密后字符串, 采用 Base64 编码
     */
    public static String encryptAes(String data, String password, String iv) {
        Validate.notBlank(password, "AES 需要传入秘钥信息");
        // AES算法的秘钥要求是16位、24位、32位
        if (!ArrayUtils.contains(VALID_AES_KEY_LENGTHS, password.length())) {
            throw new IllegalArgumentException("AES 秘钥长度要求为 16 位、24 位、32 位");
        }

        try {
            SecretKeySpec secretKey =
                    new SecretKeySpec(password.getBytes(StandardCharsets.UTF_8), AES_ALGORITHM);
            IvParameterSpec ivParameterSpec =
                    new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8));

            Cipher cipher = Cipher.getInstance(AES_TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);

            byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            log.error("AES 加密失败", e);
            return StringUtils.EMPTY;
        }
    }

    /**
     * AES 解密
     *
     * @param data 待解密数据
     * @param password 秘钥字符串
     * @return 解密后字符串
     */
    public static String decryptAes(String data, String password) {
        return decryptAes(data, password, IV_PARAMETER);
    }

    /**
     * AES 解密
     *
     * @param data 待解密数据
     * @param password 秘钥字符串
     * @param iv 偏移量
     * @return 解密后字符串
     */
    public static String decryptAes(String data, String password, String iv) {
        Validate.notBlank(password, "AES 需要传入秘钥信息");
        // AES算法的秘钥要求是16位、24位、32位

        if (!ArrayUtils.contains(VALID_AES_KEY_LENGTHS, password.length())) {
            throw new IllegalArgumentException("AES 秘钥长度要求为 16 位、24 位、32 位");
        }

        try {
            SecretKeySpec secretKey =
                    new SecretKeySpec(password.getBytes(StandardCharsets.UTF_8), AES_ALGORITHM);
            IvParameterSpec ivParameterSpec =
                    new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8));

            Cipher cipher = Cipher.getInstance(AES_TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);

            byte[] decodedBytes = Base64.getDecoder().decode(data);
            byte[] decryptedBytes = cipher.doFinal(decodedBytes);
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("AES 解密失败", e);
            return StringUtils.EMPTY;
        }
    }

    /**
     * 生成RSA密钥对（公钥和私钥）
     *
     * @return 包含公钥和私钥的 Pair 对象
     */
    public static Pair<String, String> generateRsaKeyPair() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA_ALGORITHM);
            keyPairGenerator.initialize(RSA_KEY_SIZE);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            String publicKey = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
            String privateKey =
                    Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
            return Pair.of(publicKey, privateKey);
        } catch (Exception e) {
            log.error("生成 RSA 密钥对失败", e);
            return Pair.of(StringUtils.EMPTY, StringUtils.EMPTY);
        }
    }

    /**
     * RSA 公钥加密
     *
     * @param data 待加密数据
     * @param publicKey 公钥
     * @return 加密后字符串, 采用Base64编码
     * @author lishuyan
     */
    public static String encryptRsa(String data, String publicKey) {
        Validate.notBlank(publicKey, "RSA 需要传入公钥进行加密");
        try {
            byte[] keyBytes = Base64.getDecoder().decode(publicKey);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
            PublicKey pubKey = keyFactory.generatePublic(keySpec);

            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);

            byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            log.error("RSA 公钥加密失败", e);
            return StringUtils.EMPTY;
        }
    }

    /**
     * RSA 私钥解密
     *
     * @param data 待解密数据
     * @param privateKey 私钥
     * @return 解密后字符串
     * @author lishuyan
     */
    public static String decryptRsa(String data, String privateKey) {
        Validate.notBlank(privateKey, "RSA 需要传入私钥进行解密");

        try {
            byte[] keyBytes = Base64.getDecoder().decode(privateKey);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
            PrivateKey privKey = keyFactory.generatePrivate(keySpec);

            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, privKey);

            byte[] decodedBytes = Base64.getDecoder().decode(data);
            byte[] decryptedBytes = cipher.doFinal(decodedBytes);
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("RSA 私钥解密失败", e);
            return StringUtils.EMPTY;
        }
    }
}
