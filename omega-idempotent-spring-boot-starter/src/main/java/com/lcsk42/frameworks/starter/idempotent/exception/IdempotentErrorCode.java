package com.lcsk42.frameworks.starter.idempotent.exception;

import com.lcsk42.frameworks.starter.convention.enums.BusinessDomainEnum;
import com.lcsk42.frameworks.starter.convention.enums.ErrorSourceEnum;
import com.lcsk42.frameworks.starter.convention.errorcode.ErrorCode;
import com.lcsk42.frameworks.starter.convention.model.ErrorNumber;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum IdempotentErrorCode implements ErrorCode {

    IDEMPOTENT_TOKEN_NULL_ERROR(ErrorSourceEnum.CLIENT,
            BusinessDomainEnum.IDEMPOTENT,
            ErrorNumber.of(1),
            "幂等 Token 为空"),

    IDEMPOTENT_TOKEN_DELETE_ERROR(ErrorSourceEnum.CLIENT,
            BusinessDomainEnum.IDEMPOTENT,
            ErrorNumber.of(2),
            "幂等 Token 已被使用或失效"),

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
