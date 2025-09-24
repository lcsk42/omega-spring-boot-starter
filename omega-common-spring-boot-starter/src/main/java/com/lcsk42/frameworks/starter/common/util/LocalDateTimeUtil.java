package com.lcsk42.frameworks.starter.common.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * LocalDateTime 转换和操作工具类。 提供跨不同时区处理时间戳的方法。
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class LocalDateTimeUtil {
    /**
     * 获取系统默认时区的当前日期时间。
     *
     * @return 当前 LocalDateTime
     */
    public static LocalDateTime now() {
        return LocalDateTime.now();
    }

    /**
     * 将纪元毫秒数转换为系统默认时区的 LocalDateTime。
     *
     * @param epochMilli 从 1970-01-01T00:00:00Z 开始的毫秒数
     * @return 对应的 LocalDateTime
     */
    public static LocalDateTime of(long epochMilli) {
        return of(Instant.ofEpochMilli(epochMilli));
    }

    /**
     * 将 Instant 转换为系统默认时区的 LocalDateTime。
     *
     * @param instant 要转换的 Instant
     * @return 对应的 LocalDateTime（输入为 null 时返回 null）
     */
    public static LocalDateTime of(Instant instant) {
        return of(instant, ZoneId.systemDefault());
    }

    /**
     * 将 Instant 转换为 UTC 时区的 LocalDateTime。
     *
     * @param instant 要转换的 Instant
     * @return UTC 时区对应的 LocalDateTime（输入为 null 时返回 null）
     */
    public static LocalDateTime ofUTC(Instant instant) {
        return of(instant, ZoneId.of("UTC"));
    }

    /**
     * 将 Instant 转换为指定时区的 LocalDateTime。 如果 zoneId 为 null 则使用系统默认时区。
     *
     * @param instant 要转换的 Instant
     * @param zoneId 目标时区
     * @return 对应的 LocalDateTime（instant 为 null 时返回 null）
     */
    public static LocalDateTime of(Instant instant, ZoneId zoneId) {
        if (null == instant) {
            return null;
        }
        return LocalDateTime.ofInstant(instant,
                ObjectUtils.defaultIfNull(zoneId, ZoneId.systemDefault()));
    }

    /**
     * 将 LocalDateTime 转换为系统默认时区的纪元秒数。
     *
     * @param localDateTime 要转换的日期时间
     * @return 从 1970-01-01T00:00:00Z 开始的秒数（输入为 null 时返回 null）
     */
    public static Long toEpochMilli(LocalDateTime localDateTime) {
        if (null == localDateTime) {
            return null;
        }
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().getEpochSecond();
    }

    /**
     * 基本 ISO 日期格式(yyyyMMdd)的格式化器（不含分隔符）。 此格式遵循 ISO-8601 扩展日期格式，但省略了连字符。
     * <p>
     * 示例：2023年12月25日格式化为 "20231225"。
     *
     * @see DateTimeFormatter#ISO_LOCAL_DATE
     */
    public static final DateTimeFormatter BASIC_ISO_DATE_FORMATTER;

    /**
     * 标准日期格式：yyyyMMddHHmmss
     */
    public static final DateTimeFormatter PURE_DATETIME_PATTERN;

    static {
        BASIC_ISO_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
        PURE_DATETIME_PATTERN = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    }
}
