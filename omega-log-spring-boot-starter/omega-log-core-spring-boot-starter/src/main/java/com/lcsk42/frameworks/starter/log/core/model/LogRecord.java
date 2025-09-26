package com.lcsk42.frameworks.starter.log.core.model;

import com.lcsk42.frameworks.starter.json.jackson.util.JacksonUtil;
import com.lcsk42.frameworks.starter.log.core.enums.Include;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;

/**
 * 日志信息
 */
@Getter
@Setter
public class LogRecord {
    /**
     * 描述
     */
    private String description;

    /**
     * 模块
     */
    private String module;

    /**
     * 请求信息
     */
    private LogRequest request;

    /**
     * 响应信息
     */
    private LogResponse response;

    /**
     * 耗时
     */
    private Duration timeTaken;

    /**
     * 时间戳
     */
    private final Instant timestamp;

    /**
     * 错误信息
     */
    private String errorMessage;

    public LogRecord(Instant timestamp, LogRequest request, LogResponse response,
            Duration timeTaken) {
        this.timestamp = timestamp;
        this.request = request;
        this.response = response;
        this.timeTaken = timeTaken;
    }

    /**
     * 开始记录日志
     *
     * @return 日志记录器
     */
    public static Recorder start() {
        return start(Instant.now());
    }

    /**
     * 开始记录日志
     *
     * @param timestamp 开始时间
     * @return 日志记录器
     */
    public static Recorder start(Instant timestamp) {
        return new Recorder(timestamp);
    }

    @RequiredArgsConstructor
    public static final class Recorder {
        // 当前时间戳
        private final Instant timestamp;

        /**
         * 结束日志记录
         *
         * @param timestamp 结束时间
         * @param includes 包含信息
         * @return 日志记录
         */
        public LogRecord finish(Instant timestamp, Set<Include> includes) {
            LogRequest logRequest = new LogRequest(includes);
            LogResponse logResponse = new LogResponse(includes);
            Duration duration = Duration.between(this.timestamp, timestamp);
            return new LogRecord(this.timestamp, logRequest, logResponse, duration);
        }
    }

    @Override
    public String toString() {
        return JacksonUtil.toPrettyJson(this);
    }
}
