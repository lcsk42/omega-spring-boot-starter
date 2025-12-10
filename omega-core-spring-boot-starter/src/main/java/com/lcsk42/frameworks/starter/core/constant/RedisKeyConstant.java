package com.lcsk42.frameworks.starter.core.constant;

import com.lcsk42.frameworks.starter.core.util.CacheUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RedisKeyConstant {

    public final static String ROOT_PREFIX = "framework";

    public static class User {
        private final static String PREFIX = CacheUtil.buildKey(ROOT_PREFIX, "user");

        public static String getUserAccessToken(Long id) {
            return CacheUtil.buildKey(PREFIX, "access-token", id.toString());
        }

        public static String getUserRefreshToken(Long id) {
            return CacheUtil.buildKey(PREFIX, "refresh-token", id.toString());
        }

        public static String getUserBlockToken(String token) {
            return CacheUtil.buildKey(PREFIX, "block-token", token);
        }
    }
}
