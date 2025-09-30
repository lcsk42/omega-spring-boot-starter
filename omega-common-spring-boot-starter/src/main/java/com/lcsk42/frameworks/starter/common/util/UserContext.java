package com.lcsk42.frameworks.starter.common.util;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.lcsk42.frameworks.starter.convention.model.BaseUserInfoDTO;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Optional;
import java.util.function.Function;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserContext {

    private static final ThreadLocal<Object> USER_THREAD_LOCAL = new TransmittableThreadLocal<>();

    /**
     * 设置用户至上下文
     *
     * @param user 用户详情信息
     */
    public static <T extends BaseUserInfoDTO> void setUser(T user) {
        USER_THREAD_LOCAL.set(user);
    }

    /**
     * 用户上下文中获取用户信息
     *
     * @return 用户详情信息
     */
    @SuppressWarnings("unchecked")
    public static <T extends BaseUserInfoDTO> T getUser() {
        return (T) USER_THREAD_LOCAL.get();
    }

    /**
     * 获取用户的唯一标识
     *
     * @return userId
     */
    public static Long getUserId() {
        return getProperty(BaseUserInfoDTO::getUserId);
    }

    /**
     * 获取上下文中用户特定属性
     *
     * @param propertyGetter 属性获取函数
     * @return 用户属性值
     */
    public static <T extends BaseUserInfoDTO, R> R getProperty(Function<T, R> propertyGetter) {
        T user = getUser();
        return Optional.ofNullable(user).map(propertyGetter).orElse(null);
    }

    /**
     * 清理用户上下文
     */
    public static void removeUser() {
        USER_THREAD_LOCAL.remove();
    }
}
