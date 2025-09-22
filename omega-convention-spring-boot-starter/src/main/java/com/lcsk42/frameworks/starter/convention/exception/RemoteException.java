package com.lcsk42.frameworks.starter.convention.exception;

import com.lcsk42.frameworks.starter.convention.errorcode.BaseErrorCode;
import com.lcsk42.frameworks.starter.convention.errorcode.ErrorCode;

/**
 * RemoteException
 * <p>
 * 远程异常
 * <p>
 * 表示在远程或第三方服务交互期间发生的异常。 它封装了标准化的 {@link ErrorCode} 以实现一致的错误处理。
 */
public class RemoteException extends AbstractException {

  /**
   * 使用 {@link BaseErrorCode#REMOTE_ERROR} 构造一个默认的远程异常。
   */
  public RemoteException() {
    this(BaseErrorCode.REMOTE_ERROR);
  }

  /**
   * 使用特定错误代码构造远程异常。
   *
   * @param errorCode 关联的错误代码。
   */
  public RemoteException(ErrorCode errorCode) {
    this(null, null, errorCode);
  }

  /**
   * 使用自定义消息和默认远程错误代码构造远程异常。
   *
   * @param message 异常消息。
   */
  public RemoteException(String message) {
    this(message, null, BaseErrorCode.REMOTE_ERROR);
  }

  /**
   * 使用自定义消息和特定错误代码构造远程异常。
   *
   * @param message 异常消息。
   * @param errorCode 关联的错误代码。
   */
  public RemoteException(String message, ErrorCode errorCode) {
    this(message, null, errorCode);
  }

  /**
   * 使用完整参数构造远程异常。
   *
   * @param message 异常消息。
   * @param throwable 异常的底层原因。
   * @param errorCode 关联的错误代码。
   */
  public RemoteException(String message, Throwable throwable, ErrorCode errorCode) {
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
        RemoteException {
            code='%s',
            message='%s'
        }""".formatted(errorCode, errorMessage);
  }
}
