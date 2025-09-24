package com.lcsk42.frameworks.starter.convention.exception;

import com.lcsk42.frameworks.starter.convention.errorcode.BaseErrorCode;
import com.lcsk42.frameworks.starter.convention.errorcode.ErrorCode;

/**
 * 客户端异常
 * <p>
 * 表示源于客户端错误的异常。 该异常封装了 {@link ErrorCode} 以提供标准化的错误处理。
 * </p>
 */
public class ClientException extends AbstractException {

    /**
     * 使用 {@link BaseErrorCode#CLIENT_ERROR} 构造默认的客户端异常。
     */
    public ClientException() {
        this(BaseErrorCode.CLIENT_ERROR);
    }

    /**
     * 使用特定错误代码构造客户端异常。
     *
     * @param errorCode 关联的错误代码
     */
    public ClientException(ErrorCode errorCode) {
        this(null, null, errorCode);
    }

    /**
     * 用自定义消息和默认错误代码构造客户端异常。
     *
     * @param message 异常消息
     */
    public ClientException(String message) {
        this(message, null, BaseErrorCode.CLIENT_ERROR);
    }

    /**
     * 用自定义消息和特定错误代码构造客户端异常。
     *
     * @param message 异常消息
     * @param errorCode 关联的错误代码
     */
    public ClientException(String message, ErrorCode errorCode) {
        this(message, null, errorCode);
    }

    /**
     * 用完整参数构造客户端异常。
     *
     * @param message 异常消息
     * @param throwable 异常的底层原因
     * @param errorCode 关联的错误代码
     */
    public ClientException(String message, Throwable throwable, ErrorCode errorCode) {
        super(message, throwable, errorCode);
    }

    /**
     * 返回异常的字符串表示形式。
     *
     * @return 包含错误代码和消息的字符串
     */
    @Override
    public String toString() {
        return """
                ClientException {
                    code='%s',
                    message='%s'
                }""".formatted(errorCode, errorMessage);
    }
}
