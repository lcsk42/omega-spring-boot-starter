package com.lcsk42.frameworks.starter.convention.exception;

import com.lcsk42.frameworks.starter.convention.errorcode.BaseErrorCode;
import com.lcsk42.frameworks.starter.convention.errorcode.ErrorCode;

/**
 * ServiceException
 * <p>
 * 服务异常
 * <p>
 * 表示在内部服务逻辑执行期间发生的异常。 封装了 {@link ErrorCode} 以提供结构化的错误信息。
 */
public class ServiceException extends AbstractException {

    /**
     * 使用 {@link BaseErrorCode#SERVICE_ERROR} 构造一个默认的服务异常。
     */
    public ServiceException() {
        this(BaseErrorCode.SERVICE_ERROR);
    }

    /**
     * 使用特定错误代码构造服务异常。
     *
     * @param errorCode 关联的错误代码。
     */
    public ServiceException(ErrorCode errorCode) {
        this(null, errorCode);
    }

    /**
     * 使用自定义消息和默认服务错误代码构造服务异常。
     *
     * @param message 异常消息。
     */
    public ServiceException(String message) {
        this(message, null, BaseErrorCode.SERVICE_ERROR);
    }

    /**
     * 使用自定义消息和特定错误代码构造服务异常。
     *
     * @param message 异常消息。
     * @param errorCode 关联的错误代码。
     */
    public ServiceException(String message, ErrorCode errorCode) {
        this(message, null, errorCode);
    }

    /**
     * 使用完整参数构造服务异常。 如果 message 为 null，将使用 errorCode 中的默认消息。
     *
     * @param message 异常消息。
     * @param throwable 异常的底层原因。
     * @param errorCode 关联的错误代码。
     */
    public ServiceException(String message, Throwable throwable, ErrorCode errorCode) {
        super(message, throwable, errorCode);
    }

    /**
     * 返回异常的字符串表示形式。
     *
     * @return 包含错误代码和消息的字符串。
     */
    @Override
    public String toString() {
        return """
                ServiceException {
                    code='%s',
                    message='%s'
                }""".formatted(errorCode, errorMessage);
    }
}
