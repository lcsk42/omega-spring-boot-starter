package com.lcsk42.frameworks.starter.log.core.enums;

import java.util.Set;

/**
 * 日志包含信息
 */
public enum Include {

    /**
     * 所有项
     */
    ALL,

    /**
     * 描述
     */
    DESCRIPTION,

    /**
     * 模块
     */
    MODULE,

    /**
     * 请求头（默认）
     */
    REQUEST_HEADERS,

    /**
     * 请求体
     */
    REQUEST_BODY,

    /**
     * 请求参数（默认）
     */
    REQUEST_PARAM,

    /**
     * IP 归属地
     */
    IP_ADDRESS,

    /**
     * 浏览器
     */
    BROWSER,

    /**
     * 操作系统
     */
    OS,

    /**
     * 响应头（默认）
     */
    RESPONSE_HEADERS,

    /**
     * 响应体
     */
    RESPONSE_BODY,

    /**
     * 响应参数（默认）
     */
    RESPONSE_PARAM,
    ;

    private static final Set<Include> DEFAULT_INCLUDES = Set.of(
            Include.REQUEST_HEADERS,
            Include.REQUEST_PARAM,
            Include.RESPONSE_HEADERS,
            Include.RESPONSE_PARAM);


    /**
     * 获取默认包含信息
     *
     * @return 默认包含信息
     */
    public static Set<Include> defaultIncludes() {
        return DEFAULT_INCLUDES;
    }
}
