package com.lcsk42.frameworks.starter.common.util;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.lcsk42.frameworks.starter.convention.model.BaseUserInfoDTO;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserContext {

    private static final ThreadLocal<BaseUserInfoDTO> USER_THREAD_LOCAL =
            new TransmittableThreadLocal<>();

    /**
     * 设置用户至上下文
     *
     * @param user 用户详情信息
     */
    public static void setUser(BaseUserInfoDTO user) {
        USER_THREAD_LOCAL.set(user);
    }

    /**
     * 用户上下文中获取用户信息
     *
     * @return 用户详情信息
     */
    public static BaseUserInfoDTO getUser() {
        return USER_THREAD_LOCAL.get();
    }

    /**
     * 获取用户的唯一标识
     *
     * @return userId
     */
    public static Long getUserId() {
        return getUser().getUserId();
    }

    /**
     * 清理用户上下文
     */
    public static void removeUser() {
        USER_THREAD_LOCAL.remove();
    }
}
