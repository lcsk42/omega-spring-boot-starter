package com.lcsk42.frameworks.starter.convention.errorcode;

import com.lcsk42.frameworks.starter.convention.enums.BusinessDomainEnum;
import com.lcsk42.frameworks.starter.convention.enums.ErrorSourceEnum;
import com.lcsk42.frameworks.starter.convention.model.ErrorNumber;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SqlErrorCode implements ErrorCode {

    RECORD_NOT_FOUND_EXCEPTION(
            ErrorSourceEnum.CLIENT,
            BusinessDomainEnum.SQL,
            ErrorNumber.of(404),
            "Record not found"),
    DUPLICATE_KEY(
            ErrorSourceEnum.CLIENT,
            BusinessDomainEnum.SQL,
            ErrorNumber.of(409),
            "Duplicate key violation"),
    DATA_INTEGRITY_VIOLATION(
            ErrorSourceEnum.CLIENT,
            BusinessDomainEnum.SQL,
            ErrorNumber.of(422),
            "Data integrity violation"),
    QUERY_TIMEOUT(
            ErrorSourceEnum.CLIENT,
            BusinessDomainEnum.SQL,
            ErrorNumber.of(408),
            "Query timeout"),
    TRANSACTION_ROLLBACK(
            ErrorSourceEnum.CLIENT,
            BusinessDomainEnum.SQL,
            ErrorNumber.of(500),
            "Transaction rollback"),

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
