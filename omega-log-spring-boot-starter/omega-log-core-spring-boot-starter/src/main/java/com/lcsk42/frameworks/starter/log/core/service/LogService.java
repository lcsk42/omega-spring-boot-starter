package com.lcsk42.frameworks.starter.log.core.service;

import com.lcsk42.frameworks.starter.log.core.model.LogRecord;

/**
 * 日志持久层接口
 */
public interface LogService {
    /**
     * 实际处理日志信息
     *
     * @param logRecord 日志信息
     */
    void handle(LogRecord logRecord);
}
