package com.lcsk42.frameworks.starter.convention.exception;

import com.lcsk42.frameworks.starter.convention.errorcode.ErrorCode;
import lombok.Getter;
import org.springframework.util.StringUtils;

/**
 * AbstractException
 * <p>
 * 自定义运行时异常的基类，携带标准化的错误代码和错误信息。
 * </p>
 */
@Getter
public abstract class AbstractException extends RuntimeException {

    /**
     * 表示错误类型的标准化错误代码。
     */
    public final String errorCode;

    /**
     * 描述异常的详细错误信息。
     */
    public final String errorMessage;

    /**
     * 使用自定义消息、原因和错误代码构造新的 AbstractException。
     *
     * @param message 异常的自定义消息
     * @param throwable 异常的根源原因
     * @param errorCode 关联的错误代码
     */
    protected AbstractException(String message, Throwable throwable, ErrorCode errorCode) {
        super(message, throwable);
        this.errorCode = errorCode.getCode();
        this.errorMessage = StringUtils.hasLength(message) ? message : errorCode.getMessage();
    }
}
