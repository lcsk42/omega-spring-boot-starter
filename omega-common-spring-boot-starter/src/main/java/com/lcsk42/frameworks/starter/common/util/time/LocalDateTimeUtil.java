package com.lcsk42.frameworks.starter.common.util.time;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

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
     * 创建并为 {@link DateTimeFormatter} 赋予默认时区和位置信息，默认值为系统默认值。
     *
     * @param pattern 日期格式
     * @return {@link DateTimeFormatter}
     */
    public static DateTimeFormatter createFormatter(String pattern) {
        return DateTimeFormatter.ofPattern(pattern, Locale.getDefault())
                .withZone(ZoneId.systemDefault());
    }

    /**
     * 纯(无分割符)日期格式：yyyyMMdd
     */
    public static final DateTimeFormatter PURE_DATE;

    /**
     * 纯(无分割符)日期格式：HHmmss
     */
    public static final DateTimeFormatter PURE_TIME;

    /**
     * 纯(无分割符)日期格式：yyyyMMddHHmmss
     */
    public static final DateTimeFormatter PURE_DATE_TIME;

    /**
     * 标准日期时间格式，精确到毫秒：yyyy-MM-dd HH:mm:ss
     */
    public static final DateTimeFormatter NORM_DATE_TIME;

    /**
     * 标准日期时间格式，精确到毫秒：yyyy-MM-dd HH:mm:ss.SSS
     */
    public static final DateTimeFormatter NORM_DATE_TIME_MS;

    /**
     * 标准日期格式：yyyy年MM月dd日
     */
    public static final DateTimeFormatter CHINESE_DATE;

    /**
     * 标准日期格式：yyyy年MM月dd日 HH时mm分ss秒
     */
    public static final DateTimeFormatter CHINESE_DATE_TIME;

    static {
        PURE_DATE = createFormatter("yyyyMMdd");
        PURE_TIME = createFormatter("HHmmss");
        PURE_DATE_TIME = createFormatter("yyyyMMddHHmmss");
        NORM_DATE_TIME = createFormatter("yyyy-MM-dd HH:mm:ss");
        NORM_DATE_TIME_MS = createFormatter("yyyy-MM-dd HH:mm:ss.SSS");
        CHINESE_DATE = createFormatter("yyyy年MM月dd日");
        CHINESE_DATE_TIME = createFormatter("yyyy年MM月dd日HH时mm分ss秒");
    }
}
