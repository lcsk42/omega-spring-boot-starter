package com.lcsk42.frameworks.starter.web;

import com.lcsk42.frameworks.starter.convention.errorcode.BaseErrorCode;
import com.lcsk42.frameworks.starter.convention.exception.AbstractException;
import com.lcsk42.frameworks.starter.convention.model.Result;
import com.lcsk42.frameworks.starter.core.constant.HttpHeaderConstant;
import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Optional;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 错误消息的日志模板。 日志格式为: [HTTP 方法] URL [异常] 错误消息
     */
    private static final String ERROR_LOG_TEMPLATE = "[{}] {} [ex] {}";

    /**
     * 处理参数校验异常（如来自 @Valid 注解的异常）。 用于捕获客户端传递无效方法参数相关的错误。
     */
    @SneakyThrows
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public Result<Void> validExceptionHandler(HttpServletRequest request,
            MethodArgumentNotValidException ex) {
        // 从第一个字段错误中提取校验错误信息
        BindingResult bindingResult = ex.getBindingResult();
        FieldError firstFieldError = bindingResult.getFieldErrors().getFirst();
        String exceptionStr =
                Optional.ofNullable(firstFieldError).map(FieldError::getDefaultMessage)
                        .orElse(StringUtils.EMPTY);
        // 记录错误详情
        log.error(ERROR_LOG_TEMPLATE, request.getMethod(), getUrl(request), exceptionStr);
        // 返回包含错误码和消息的失败响应
        return Result.fail(BaseErrorCode.CLIENT_ERROR.getCode(), exceptionStr)
                .withRequestId(getRequestId(request));
    }

    /**
     * 处理应用程序内抛出的异常（即 AbstractException）。 用于处理应用程序特定的异常，包括自定义错误处理。
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = {AbstractException.class})
    public Result<Void> abstractException(HttpServletRequest request, AbstractException ex) {
        // 记录错误详情（包含异常原因，如果存在）
        if (ex.getCause() != null) {
            log.error(ERROR_LOG_TEMPLATE, request.getMethod(), request.getRequestURL().toString(),
                    ex, ex.getCause());
            return Result.fail(ex);
        }
        // 记录不包含原因的异常详情
        log.error(ERROR_LOG_TEMPLATE, request.getMethod(), request.getRequestURL().toString(),
                ex.toString());
        return Result.fail(ex).withRequestId(getRequestId(request));
    }

    /**
     * 处理应用程序未捕获的异常。 当发生预期之外的错误或未明确捕获的异常时触发。
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = Throwable.class)
    public Result<Void> defaultErrorHandler(HttpServletRequest request, Throwable throwable) {
        // 记录未捕获的异常
        log.error(ERROR_LOG_TEMPLATE, request.getMethod(), getUrl(request), throwable.toString(),
                throwable);
        return Result.fail(throwable.getMessage()).withRequestId(getRequestId(request));
    }

    /**
     * 辅助方法：构造完整 URL（包含查询字符串，如果存在）
     */
    private String getUrl(HttpServletRequest request) {
        if (StringUtils.isEmpty(request.getQueryString())) {
            return request.getRequestURL().toString();
        }
        return request.getRequestURL().toString() + "?" + request.getQueryString();
    }

    private String getRequestId(HttpServletRequest request) {
        String requestId = request.getHeader(HttpHeaderConstant.REQUEST_ID);
        if (StringUtils.isBlank(requestId)) {
            requestId = HttpHeaderConstant.getExceptionRequestId();
        }
        return requestId;
    }
}
