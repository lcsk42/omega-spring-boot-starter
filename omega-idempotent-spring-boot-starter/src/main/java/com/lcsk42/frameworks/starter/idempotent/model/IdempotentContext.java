package com.lcsk42.frameworks.starter.idempotent.model;

import com.google.common.collect.Maps;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.MapUtils;

import java.util.Map;
import java.util.Optional;

/**
 * 幂等上下文
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class IdempotentContext {

    private static final ThreadLocal<Map<String, Object>> CONTEXT = new ThreadLocal<>();

    public static Map<String, Object> getMap() {
        return CONTEXT.get();
    }

    public static Object getKey(String key) {
        return Optional.ofNullable(getMap()).map(map -> MapUtils.getObject(map, key)).orElse(null);
    }

    public static String getString(String key) {
        return Optional.ofNullable(getMap())
                .map(map -> MapUtils.getString(map, key))
                .orElse(null);
    }

    public static void put(String key, Object val) {
        Map<String, Object> context = getMap();
        if (MapUtils.isEmpty(context)) {
            context = Maps.newHashMap();
        }
        context.put(key, val);
        putContext(context);
    }

    public static void putContext(Map<String, Object> context) {
        Optional.ofNullable(context)
                .filter(MapUtils::isNotEmpty)
                .ifPresent(ctx -> {
                    Map<String, Object> threadContext = getMap();
                    if (MapUtils.isNotEmpty(threadContext)) {
                        threadContext.putAll(ctx);
                    } else {
                        CONTEXT.set(ctx);
                    }
                });
    }

    public static void clean() {
        CONTEXT.remove();
    }
}
