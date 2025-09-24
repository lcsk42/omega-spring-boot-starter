package com.lcsk42.frameworks.starter.convention.errorcode;

import com.lcsk42.frameworks.starter.convention.enums.BusinessDomainEnum;
import com.lcsk42.frameworks.starter.convention.enums.ErrorSourceEnum;
import com.lcsk42.frameworks.starter.convention.model.ErrorNumber;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * BaseErrorCode
 * <p>
 * 定义标准化的错误码和消息分类，按系统层级划分： - A：客户端错误（4xx） - B：服务端/内部错误（5xx） - C：外部/远程服务错误
 * </p>
 *
 * <p>
 * 每个错误码包含：
 * <ul>
 * <li>错误来源（客户端/服务端/远程）</li>
 * <li>业务域分类</li>
 * <li>域内唯一错误编号</li>
 * <li>人类可读的错误消息</li>
 * </ul>
 * </p>
 */
@Getter
@AllArgsConstructor
public enum BaseErrorCode implements ErrorCode {

    /**
     * 通用客户端错误（400）。
     * <p>
     * 当客户端输入验证失败或请求格式异常时使用。
     * </p>
     */
    CLIENT_ERROR(ErrorSourceEnum.CLIENT, BusinessDomainEnum.COMMON, ErrorNumber.of(1),
            "Client-side error: Invalid request parameters or headers"),

    /**
     * 通用系统执行错误（500）。
     * <p>
     * 用于未预期的内部服务错误。
     * </p>
     */
    SERVICE_ERROR(ErrorSourceEnum.SERVICE, BusinessDomainEnum.COMMON, ErrorNumber.of(1),
            "System execution error: Internal server failure"),

    /**
     * Feign 客户端请求错误（502）。
     * <p>
     * 当通过 Feign 客户端的服务间通信失败时使用。
     * </p>
     */
    SERVICE_FEIGN_ERROR(ErrorSourceEnum.SERVICE, BusinessDomainEnum.FEIGN, ErrorNumber.of(2),
            "Feign request failed: Service unavailable or timeout"),

    /**
     * 调用第三方远程服务失败（503）。
     * <p>
     * 当外部 API 调用失败时使用（支付网关等）。
     * </p>
     */
    REMOTE_ERROR(ErrorSourceEnum.REMOTE, BusinessDomainEnum.COMMON, ErrorNumber.of(1),
            "Failed to call third-party service: Service unavailable"),
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
