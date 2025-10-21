package com.lcsk42.frameworks.starter.log.core.model;

import com.lcsk42.frameworks.starter.log.core.config.LogProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

/**
 * 访问日志上下文
 */
@Getter
@Builder
@AllArgsConstructor
public class AccessLogContext {

    /**
     * 请求 ID
     */
    private String requestId;

    /**
     * 开始时间
     */
    private final Instant startTime;

    /**
     * 结束时间
     */
    private Instant endTime;

    /**
     * 配置信息
     */
    private final LogProperties properties;

}
