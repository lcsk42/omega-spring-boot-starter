package com.lcsk42.frameworks.starter.log.core.service;

import com.lcsk42.frameworks.starter.log.core.model.LogRecord;

import java.util.List;

/**
 * 日志持久层接口
 */
public interface LogService {
    /**
     * 查询日志列表
     *
     * @return 日志列表
     */
    default List<LogRecord> list() {
        return List.of();
    }

    /**
     * 记录日志
     *
     * @param logRecord 日志信息
     */
    default void handle(LogRecord logRecord) {
        throw new UnsupportedOperationException("Log handle not implemented");
    }
}
