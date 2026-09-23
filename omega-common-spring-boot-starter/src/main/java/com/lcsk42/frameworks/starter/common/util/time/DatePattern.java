package com.lcsk42.frameworks.starter.common.util.time;

import lombok.Getter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public enum DatePattern implements IDatePattern {

    /**
     * 年月格式：yyyy-MM
     */
    NORM_MONTH("yyyy-MM"),
    /**
     * 标准日期格式：yyyy-MM-dd
     */
    NORM_DATE("yyyy-MM-dd"),
    /**
     * 标准时间格式：HH:mm:ss
     */
    NORM_TIME("HH:mm:ss"),
    /**
     * 标准日期时间格式，精确到分：yyyy-MM-dd HH:mm
     */
    NORM_DATE_TIME_MINUTE("yyyy-MM-dd HH:mm"),
    /**
     * 标准日期时间格式，精确到秒：yyyy-MM-dd HH:mm:ss
     */
    NORM_DATE_TIME("yyyy-MM-dd HH:mm:ss"),
    /**
     * 标准日期时间格式，精确到毫秒：yyyy-MM-dd HH:mm:ss.SSS
     */
    NORM_DATE_TIME_MS("yyyy-MM-dd HH:mm:ss.SSS"),


    /**
     * 紧凑日期格式：yyyyMM
     */
    PURE_MONTH("yyyyMM"),
    /**
     * 紧凑日期格式：yyyyMMdd
     */
    PURE_DATE("yyyyMMdd"),
    /**
     * 紧凑日期格式：HHmmss
     */
    PURE_TIME("HHmmss"),
    /**
     * 紧凑日期格式：yyyyMMddHHmmss
     */
    PURE_DATE_TIME("yyyyMMddHHmmss"),
    /**
     * 紧凑日期格式：yyyyMMddHHmmssSSS
     */
    PURE_DATE_TIME_MS("yyyyMMddHHmmssSSS"),

    /**
     * 年格式：yyyy
     */
    YEAR("yyyy"),
    /**
     * 月格式：MM
     */
    MONTH("MM"),
    /**
     * 日格式：MM
     */
    DAY("dd"),
    /**
     * 时格式：HH
     */
    hour("HH"),
    /**
     * 分格式：mm
     */
    minute("mm"),
    /**
     * 秒格式：ss
     */
    second("ss"),
    /**
     * 毫秒格式：SSS
     */
    millisecond("SSS"),


    /**
     * 中文日期格式：yyyy年MM月dd日
     */
    CHINESE_DATE("yyyy年MM月dd日"),
    /**
     * 中文日期格式：yyyy年MM月dd日
     */
    CHINESE_TIME("HH时mm分ss秒"),
    /**
     * 中文日期格式：yyyy年MM月dd日 HH时mm分ss秒
     */
    CHINESE_DATE_TIME("yyyy年MM月dd日 HH时mm分ss秒"),
    ;

    DatePattern(String pattern) {
        this.pattern = pattern;
    }

    @Getter
    private final String pattern;

    private volatile DateTimeFormatter dateTimeFormatter;

    @Override
    public DateFormat getDateFormat() {
        return new SimpleDateFormat(this.pattern, Locale.getDefault(Locale.Category.FORMAT));
    }

    @Override
    public DateTimeFormatter getDateTimeFormatter() {
        DateTimeFormatter formatter = dateTimeFormatter;
        if (formatter == null) {
            synchronized (this) {
                formatter = dateTimeFormatter;
                if (formatter == null) {
                    formatter = DateTimeFormatter
                            .ofPattern(pattern, Locale.getDefault(Locale.Category.FORMAT))
                            .withZone(ZoneId.systemDefault());
                    dateTimeFormatter = formatter;
                }
            }
        }
        return formatter;
    }
}
