package com.lcsk42.frameworks.starter.ratelimiter.exception;

import com.lcsk42.frameworks.starter.convention.enums.BusinessDomainEnum;
import com.lcsk42.frameworks.starter.convention.enums.ErrorSourceEnum;
import com.lcsk42.frameworks.starter.convention.errorcode.ErrorCode;
import com.lcsk42.frameworks.starter.convention.model.ErrorNumber;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RateLimiterErrorCode implements ErrorCode {

    RATE_LIMITER_EXCEPTION(
            ErrorSourceEnum.SERVICE,
            BusinessDomainEnum.RATE_LIMITER,
            ErrorNumber.of(1),
            "服务器限流异常，请稍候再试"),
    RATE_LIMITER_KEY_EVAL_EXCEPTION(
            ErrorSourceEnum.SERVICE,
            BusinessDomainEnum.RATE_LIMITER,
            ErrorNumber.of(1),
            "限流 Key 解析错误"),
    RATE_LIMITER_GET_IP_EXCEPTION(
            ErrorSourceEnum.SERVICE,
            BusinessDomainEnum.RATE_LIMITER,
            ErrorNumber.of(1),
            "限流获取 IP 失败"),
            ;

    /**
     * 错误来源（客户端/服务端/远程）。
     */
    private final ErrorSourceEnum errorSourceEnum;

    /**
     * 错误发生的业务域。
     */
    private final BusinessDomainEnum businessDomainEnum;

    /**
     * 业务域内的唯一错误编号。
     */
    private final ErrorNumber errorNumber;

    /**
     * 人类可读的错误消息。
     * <p>
     * 应提供足够的排错上下文信息，同时确保可以安全暴露给客户端。
     * </p>
     */
    private final String message;
}
