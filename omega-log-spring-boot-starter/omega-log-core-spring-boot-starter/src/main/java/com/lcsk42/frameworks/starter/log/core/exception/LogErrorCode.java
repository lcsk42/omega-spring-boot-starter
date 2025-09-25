package com.lcsk42.frameworks.starter.log.core.exception;

import com.lcsk42.frameworks.starter.convention.enums.BusinessDomainEnum;
import com.lcsk42.frameworks.starter.convention.enums.ErrorSourceEnum;
import com.lcsk42.frameworks.starter.convention.errorcode.ErrorCode;
import com.lcsk42.frameworks.starter.convention.model.ErrorNumber;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LogErrorCode implements ErrorCode {


    MISSING_REQUEST(ErrorSourceEnum.SERVICE,
            BusinessDomainEnum.LOG,
            ErrorNumber.of(401),
            "Required request object is missing"),

    READ_REQUEST_BODY_FAILED(ErrorSourceEnum.SERVICE,
            BusinessDomainEnum.LOG,
            ErrorNumber.of(402),
            "Read request body failed"),

    MISSING_RESPONSE(ErrorSourceEnum.SERVICE,
            BusinessDomainEnum.LOG,
            ErrorNumber.of(501),
            "Required response object is missing"),
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
