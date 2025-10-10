package com.lcsk42.frameworks.starter.ratelimiter.enums;

/**
 * 限流类型
 */
public enum LimitType {

    /**
     * 全局限流
     */
    GLOBAL,

    /**
     * 根据 IP 限流
     */
    IP,

    /**
     * 根据实例限流（支持集群多实例）
     */
    CLUSTER,
    ;
}
