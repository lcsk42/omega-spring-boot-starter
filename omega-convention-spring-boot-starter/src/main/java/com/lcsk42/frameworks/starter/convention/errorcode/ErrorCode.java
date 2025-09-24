package com.lcsk42.frameworks.starter.convention.errorcode;

import com.lcsk42.frameworks.starter.convention.enums.BusinessDomainEnum;
import com.lcsk42.frameworks.starter.convention.enums.ErrorSourceEnum;
import com.lcsk42.frameworks.starter.convention.exception.AbstractException;
import com.lcsk42.frameworks.starter.convention.exception.ClientException;
import com.lcsk42.frameworks.starter.convention.exception.RemoteException;
import com.lcsk42.frameworks.starter.convention.exception.ServiceException;
import com.lcsk42.frameworks.starter.convention.model.ErrorNumber;

/**
 * 定义系统中错误代码表示的标准契约。
 * <p>
 * 该接口作为创建一致错误处理机制的基础，提供：
 * <ul>
 * <li>转换为适当异常类型的能力 ({@link ClientException}, {@link ServiceException} 等)</li>
 * <li>标准化的错误代码格式化</li>
 * <li>技术错误来源与业务领域的清晰分离</li>
 * </ul>
 * </p>
 *
 * <p>
 * 使用示例：
 *
 * <pre>{@code
 * public enum OrderErrorCodes implements ErrorCode {
 *     INVALID_ORDER(ErrorSourceEnum.CLIENT, BusinessDomainEnum.ORDER, ErrorNumber.CODE_101);
 *
 *     // ... 实现 ...
 * }
 * }</pre>
 * </p>
 */
public interface ErrorCode {

    /**
     * 根据 {@link ErrorSourceEnum} 将此错误代码转换为对应的异常类型。
     *
     * @return 匹配错误来源的具体异常实例
     * @see #toClientException()
     * @see #toServiceException()
     * @see #toRemoteException()
     */
    default AbstractException toException() {
        ErrorSourceEnum errorSourceEnum = getErrorSourceEnum();
        return switch (errorSourceEnum) {
            case CLIENT -> toClientException();
            case REMOTE -> toRemoteException();
            case SERVICE -> toServiceException();
        };
    }

    /**
     * 创建客户端错误的 {@link ClientException} (4xx 系列)。
     * <p>
     * 典型用例包括：
     * <ul>
     * <li>无效的输入参数</li>
     * <li>认证失败</li>
     * <li>请求验证错误</li>
     * </ul>
     * </p>
     */
    default ClientException toClientException() {
        return new ClientException(this);
    }

    /**
     * 创建三方集成失败的 {@link RemoteException}。
     * <p>
     * 适用于以下来源的错误：
     * <ul>
     * <li>下游服务</li>
     * <li>外部 API</li>
     * <li>基础架构组件</li>
     * </ul>
     * </p>
     */
    default RemoteException toRemoteException() {
        return new RemoteException(this);
    }

    /**
     * 创建服务端业务错误的 {@link ServiceException} (5xx 系列)。
     * <p>
     * 指示如下问题：
     * <ul>
     * <li>业务规则违反</li>
     * <li>数据一致性问题</li>
     * <li>不可恢复的处理失败</li>
     * </ul>
     * </p>
     */
    default ServiceException toServiceException() {
        return new ServiceException(this);
    }

    /**
     * 标识错误的技术来源。
     *
     * @return 分类错误来源的枚举值
     * @see ErrorSourceEnum
     */
    ErrorSourceEnum getErrorSourceEnum();

    /**
     * 指定与此错误关联的业务领域。
     *
     * @return 表示受影响业务领域的枚举值
     * @see BusinessDomainEnum
     */
    BusinessDomainEnum getBusinessDomainEnum();

    /**
     * 提供此特定错误的唯一数字标识符。
     * <p>
     * 该数字具有以下特性：
     * <ul>
     * <li>保证在其 {@link BusinessDomainEnum} 内唯一</li>
     * <li>格式化为 3 位数值 (001-999)</li>
     * <li>在编译时静态验证</li>
     * </ul>
     *
     * @see ErrorNumber
     */
    ErrorNumber getErrorNumber();

    /**
     * 生成标准格式的完整错误代码：
     * <p>
     * {@code [来源]-[领域]-[编号]}
     * </p>
     * <p>
     * 示例：{@code CLI-ORDER-101}
     * </p>
     *
     * @return 格式化后的错误代码字符串
     */
    default String getCode() {
        return getErrorSourceEnum().name() + "-" + getBusinessDomainEnum().name() + "-"
                + getErrorNumber();
    }

    /**
     * 提供错误的人类可读解释。
     * <p>
     * 错误消息应当：
     * <ul>
     * <li>简洁但具有可操作性</li>
     * <li>尽可能避免技术术语</li>
     * <li>需要动态数据时包含占位值</li>
     * </ul>
     * </p>
     *
     * @return 适合日志记录和用户显示的描述性消息
     */
    String getMessage();

}
