package com.lcsk42.frameworks.starter.log.core.service.impl;

import com.lcsk42.frameworks.starter.log.core.model.LogRecord;
import com.lcsk42.frameworks.starter.log.core.service.LogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;

@Slf4j
public class DefaultLogServiceImpl implements LogService {
    @Async
    @Override
    public void handle(LogRecord logRecord) {
        log.info("{}", logRecord);
    }
}
