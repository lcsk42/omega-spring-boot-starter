package com.lcsk42.frameworks.starter.common.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.Nonnull;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JwtUtil {

    // 默认过期时间: 10 分钟 (单位:秒)
    private static final long DEFAULT_EXPIRE_SECONDS = 10 * 60L;

    // Token 头
    public static final String TOKEN_PREFIX = "Bearer ";

    // 发行人
    private static final String ISSUER = "Omega";

    // 密钥缓存
    private static final Map<String, SecretKey> SECRET_KEY_CACHE = new ConcurrentHashMap<>();

    /**
     * 获取或创建 SecretKey
     */
    private static SecretKey getSecretKey(String secret) {
        return SECRET_KEY_CACHE.computeIfAbsent(secret,
                s -> Keys.hmacShaKeyFor(s.getBytes(StandardCharsets.UTF_8)));
    }

    /**
     * 生成 JWT Token (使用默认过期时间)
     */
    public static String generateToken(@Nonnull Map<String, Object> claims,
            @Nonnull String secret) {
        return generateToken(claims, secret, DEFAULT_EXPIRE_SECONDS);
    }

    /**
     * 生成 JWT Token
     *
     * @param claims 自定义claims
     * @param secret 密钥
     * @param expireSeconds 过期时间(秒)
     * @return token字符串
     */
    public static String generateToken(@Nonnull Map<String, Object> claims,
            @Nonnull String secret,
            long expireSeconds) {
        if (secret.isEmpty()) {
            throw new IllegalArgumentException("Secret must not be empty");
        }

        SecretKey secretKey = getSecretKey(secret);
        return Jwts.builder()
                .claims(claims)
                .signWith(secretKey)
                .issuer(ISSUER)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expireSeconds * 1_000))
                .compact();
    }

    /**
     * 解析JWT Token
     *
     * @param token Token 字符串
     * @param secret 密钥
     * @return Optional 包含 Claims 对象
     */
    public static Optional<Claims> parseToken(@Nonnull String token, @Nonnull String secret) {
        if (StringUtils.isBlank(token) || !token.startsWith(TOKEN_PREFIX)) {
            return Optional.empty();
        }

        try {
            String actualJwtToken = token.replace(TOKEN_PREFIX, StringUtils.EMPTY);
            Claims claims = Jwts.parser().verifyWith(getSecretKey(secret)).build()
                    .parseSignedClaims(actualJwtToken)
                    .getPayload();

            return claims.getExpiration().after(new Date()) ? Optional.of(claims)
                    : Optional.empty();
        } catch (JwtException ex) {
            log.warn("Invalid JWT token: {}", ex.getMessage());
            return Optional.empty();
        } catch (Exception ex) {
            log.error("Unexpected error during JWT parsing", ex);
            return Optional.empty();
        }
    }

    /**
     * 验证 Token 是否有效
     */
    public static boolean validateToken(@Nonnull String token, @Nonnull String secret) {
        return parseToken(token, secret).isPresent();
    }

    /**
     * 获取 Token 中的指定 Claim
     */
    public static <T> Optional<T> getClaim(@Nonnull String token,
            @Nonnull String secret,
            @Nonnull String claimName,
            @Nonnull Class<T> clazz) {
        return parseToken(token, secret).map(claims -> claims.get(claimName, clazz));
    }

    /**
     * 获取 Token 过期时间
     */
    public static Optional<LocalDateTime> getExpirationDate(@Nonnull String token,
            @Nonnull String secret) {
        return parseToken(token, secret).map(Claims::getExpiration).map(Date::toInstant)
                .map(instant -> instant.atZone(ZoneId.systemDefault()).toLocalDateTime());
    }

    /**
     * 判断 Token 是否过期
     */
    public static boolean isTokenExpired(@Nonnull String token, @Nonnull String secret) {
        Optional<LocalDateTime> expiration = getExpirationDate(token, secret);
        return expiration.map(e -> e.isBefore(LocalDateTime.now())).orElse(true);
    }

    /**
     * 刷新 Token（更新签发时间和过期时间）
     *
     * @param token 原token
     * @param secret 密钥
     * @return 新 Token
     */
    public static Optional<String> refreshToken(@Nonnull String token, @Nonnull String secret) {
        return parseToken(token, secret)
                .map(claims -> generateToken(claims, secret, DEFAULT_EXPIRE_SECONDS));
    }

    /**
     * 刷新 Token（更新签发时间和过期时间(默认的过期时间)）
     *
     * @param token 原token
     * @param secret 密钥
     * @param expireSeconds 新的过期时间(秒)
     * @return 新 Token
     */
    public static Optional<String> refreshToken(@Nonnull String token, @Nonnull String secret,
            long expireSeconds) {
        return parseToken(token, secret)
                .map(claims -> generateToken(claims, secret, expireSeconds));
    }
}
