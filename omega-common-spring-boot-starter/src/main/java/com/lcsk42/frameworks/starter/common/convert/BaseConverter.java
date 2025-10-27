package com.lcsk42.frameworks.starter.common.convert;


import com.lcsk42.frameworks.starter.common.util.time.LocalDateTimeUtil;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;

public interface BaseConverter {
    /**
     * 默认方法：将 LocalDateTime 转换为毫秒时间戳。
     *
     * @param localDateTime 要转换的 LocalDateTime
     * @return 毫秒时间戳，输入为 null 时返回 null
     */
    default Long toLong(LocalDateTime localDateTime) {
        return Optional.ofNullable(localDateTime)
                .map(LocalDateTimeUtil::toEpochMilli)
                .orElse(null);
    }

    /**
     * 默认方法：将毫秒时间戳转换为 LocalDateTime。
     *
     * @param localDateTime 毫秒时间戳
     * @return LocalDateTime 实例，输入为 null 时返回 null
     */
    default LocalDateTime toLocalDateTime(Long localDateTime) {
        return Optional.ofNullable(localDateTime)
                .map(LocalDateTimeUtil::of)
                .orElse(null);
    }

    /**
     * 默认方法：将 LocalDateTime 格式化为字符串。
     *
     * @param localDateTime 要格式化的 LocalDateTime
     * @param dateTimeFormatter 使用的格式化器（为 null 时默认为 ISO_LOCAL_DATE_TIME）
     * @return 格式化后的字符串，输入为 null 时返回 null
     */
    default String toString(LocalDateTime localDateTime, DateTimeFormatter dateTimeFormatter) {
        if (null == localDateTime) {
            return null;
        }
        return Objects.requireNonNullElse(dateTimeFormatter, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                .format(localDateTime);
    }

    /**
     * 默认方法：将字符串解析为 LocalDateTime。
     *
     * @param localDateTime 要解析的字符串
     * @param dateTimeFormatter 使用的格式化器（为 null 时默认为 ISO_LOCAL_DATE_TIME）
     * @return 解析后的 LocalDateTime，输入为空时返回 null
     */
    default LocalDateTime toLocalDateTime(String localDateTime,
            DateTimeFormatter dateTimeFormatter) {
        if (StringUtils.isBlank(localDateTime)) {
            return null;
        }
        return LocalDateTime.parse(localDateTime,
                Objects.requireNonNullElse(dateTimeFormatter,
                        DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }
}
