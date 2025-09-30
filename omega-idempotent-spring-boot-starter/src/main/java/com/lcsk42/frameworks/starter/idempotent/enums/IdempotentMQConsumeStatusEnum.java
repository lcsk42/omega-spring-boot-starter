package com.lcsk42.frameworks.starter.idempotent.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * 幂等 MQ 消费状态枚举
 */
@Getter
@AllArgsConstructor
public enum IdempotentMQConsumeStatusEnum {

    /**
     * 消费中
     */
    CONSUMING("consuming"),

    /**
     * 已消费
     */
    CONSUMED("consumed");

    private final String status;

    /**
     * 如果消费状态等于消费中，返回失败
     *
     * @param consumeStatus 消费状态
     * @return 是否消费失败
     */
    public static boolean isError(String consumeStatus) {
        return Objects.equals(CONSUMING.status, consumeStatus);
    }
}
