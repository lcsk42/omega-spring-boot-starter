package com.lcsk42.frameworks.starter.log.interceptor.interceptor;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.lcsk42.frameworks.starter.log.core.config.LogProperties;
import com.lcsk42.frameworks.starter.log.core.handler.LogHandler;
import com.lcsk42.frameworks.starter.log.core.model.AccessLogContext;
import com.lcsk42.frameworks.starter.log.core.model.LogRecord;
import com.lcsk42.frameworks.starter.log.core.service.LogService;
import com.lcsk42.frameworks.starter.log.core.util.LogUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.reflect.Method;
import java.time.Instant;

@Slf4j
@RequiredArgsConstructor
public class LogInterceptor implements HandlerInterceptor {
    private final LogProperties logProperties;
    private final LogHandler logHandler;
    private final LogService logService;
    private final TransmittableThreadLocal<LogRecord.Recorder> logTtl =
            new TransmittableThreadLocal<>();

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Object handler) {
        Instant startTime = Instant.now();
        logHandler.accessLogStart(AccessLogContext.builder()
                .requestId(LogUtil.getRequestId())
                .startTime(startTime)
                .properties(logProperties)
                .build());
        // 开始日志记录
        if (this.isRecord(handler)) {
            LogRecord.Recorder startedLogRecord = logHandler.start(startTime);
            logTtl.set(startedLogRecord);
        }
        return true;
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Object handler,
            Exception e) {
        try {
            Instant endTime = Instant.now();
            logHandler.accessLogFinish(AccessLogContext.builder()
                    .endTime(endTime)
                    .build());
            LogRecord.Recorder startedLogRecord = logTtl.get();
            if (startedLogRecord == null) {
                return;
            }
            // 结束日志记录
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method targetMethod = handlerMethod.getMethod();
            Class<?> targetClass = handlerMethod.getBeanType();
            LogRecord logRecord = logHandler.finish(
                    startedLogRecord,
                    endTime,
                    logProperties.getIncludes(),
                    targetMethod,
                    targetClass);
            logService.handle(logRecord);
        } catch (Exception ex) {
            log.error("Logging http log occurred an error: {}.", ex.getMessage(), ex);
            throw ex;
        } finally {
            logTtl.remove();
        }
    }

    /**
     * 是否记录日志
     *
     * @param handler 处理器
     * @return true：需要记录；false：不需要记录
     */
    private boolean isRecord(Object handler) {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return false;
        }
        return logHandler.isRecord(handlerMethod.getMethod(), handlerMethod.getBeanType());
    }
}
