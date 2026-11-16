package com.lcsk42.frameworks.starter.common.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 时间名称模板工具类。
 * <p>
 * 支持在文件名/目录名等模板中通过 {@code {...}} 占位符嵌入时间信息，
 * 占位符内可使用 JDK 日期时间 pattern（如 {@code yyyyMMdd}），
 * 也可使用内置快捷符（如 {@code date}、{@code datetime}、{@code timestamp} 等）。
 * 格式化完成后会自动将非法文件名字符替换为下划线。
 * </p>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TimeStringTemplateUtil {

    /**
     * 占位符匹配模式：匹配 {...} 形式的内容，花括号内不允许再嵌套花括号
     */
    private static final Pattern PLACEHOLDER = Pattern.compile("\\{([^{}]*)}");

    /**
     * 快捷符到真实日期时间 pattern 的映射，按插入顺序保存
     */
    private static final Map<String, String> SHORTCUT = new LinkedHashMap<>();

    static {
        SHORTCUT.put("date", "yyyyMMdd");                   // 20261116
        SHORTCUT.put("time", "HHmmss");                     // 153301
        SHORTCUT.put("datetime", "yyyyMMddHHmmss");         // 20261116153301
        SHORTCUT.put("timestamp", "yyyyMMddHHmmssSSS");     // 20261116153301123
        SHORTCUT.put("iso", "yyyy-MM-dd'T'HH:mm:ss");       // 2026-11-16T15:33:01
        SHORTCUT.put("year", "yyyy");
        SHORTCUT.put("month", "MM");
        SHORTCUT.put("day", "dd");
        SHORTCUT.put("hour", "HH");
        SHORTCUT.put("minute", "mm");
        SHORTCUT.put("second", "ss");
    }

    /**
     * DateTimeFormatter 缓存，避免相同 pattern 重复创建格式化器
     */
    private static final Map<String, DateTimeFormatter> CACHE = new ConcurrentHashMap<>();

    /**
     * 文件名/路径中的非法字符（含控制字符），匹配后统一替换为下划线
     */
    private static final Pattern ILLEGAL_CHARS = Pattern.compile("[\\\\/:*?\"<>|\\p{Cntrl}]");


    /**
     * 获取（或创建并缓存）指定 pattern 对应的 {@link DateTimeFormatter}。
     *
     * @param pattern JDK 日期时间格式 pattern，如 {@code yyyyMMddHHmmss}
     * @return 对应的格式化器
     * @throws IllegalArgumentException pattern 非法时抛出
     */
    private static DateTimeFormatter formatterOf(String pattern) {
        return CACHE.computeIfAbsent(pattern, DateTimeFormatter::ofPattern);
    }

    /**
     * 清理字符串中的非法字符，将文件系统不允许的字符（如 {@code \ / : * ? " < > |} 及控制字符）
     * 替换为下划线 {@code _}。
     *
     * @param name 原始字符串，可为 null
     * @return 清理后的字符串；入参为 null 时返回 null
     */
    private static String sanitize(String name) {
        if (name == null) return null;
        return ILLEGAL_CHARS.matcher(name).replaceAll("_");
    }

    /**
     * 使用当前时间格式化模板。
     *
     * @param template 名称模板，可包含 {@code {...}} 占位符
     * @return 格式化并清理非法字符后的结果
     */
    public static String format(String template) {
        return format(template, LocalDateTime.now());
    }

    /**
     * 替换模板中的 {@code {...}} 占位符。
     * <p>
     * 占位符内的 key 优先按快捷符解析，找不到则视为 JDK 日期时间 pattern；
     * 空占位符 {@code {}} 替换为空串；pattern 非法时原样保留占位符，保证导出不中断。
     *
     * @param template      名称模板
     * @param localDateTime 用于格式化的时间
     * @return 替换占位符后的字符串（尚未清理非法字符）
     */
    private static String replacePlaceholders(String template, LocalDateTime localDateTime) {
        Matcher m = PLACEHOLDER.matcher(template);
        StringBuilder sb = new StringBuilder();

        while (m.find()) {
            String key = m.group(1).trim();
            String replacement;
            if (key.isEmpty()) {
                replacement = StringUtils.EMPTY;                     // {} 视为空
            } else {
                // 快捷符 -> 真实 pattern
                String realPattern = SHORTCUT.getOrDefault(key.toLowerCase(), key);
                try {
                    replacement = localDateTime.format(formatterOf(realPattern));
                } catch (IllegalArgumentException | DateTimeParseException e) {
                    // pattern 非法：原样保留占位符，导出不中断
                    replacement = m.group(0);
                }
            }
            m.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        m.appendTail(sb);
        return sb.toString();
    }

    /**
     * 用指定时间格式化名称模板。
     * <p>
     * 处理流程：先替换 {@code {...}} 占位符（支持快捷符），再清理非法字符。
     *
     * @param template      名称模板，可为 null 或空串（原样返回）
     * @param localDateTime 用于格式化的时间，为 null 时使用当前时间
     * @return 格式化并清理非法字符后的结果
     */
    public static String format(String template, LocalDateTime localDateTime) {
        if (template == null || template.isEmpty()) {
            return template;
        }
        if (localDateTime == null) {
            localDateTime = LocalDateTime.now();
        }

        // 1. 先处理 {...} 占位符（含快捷符）
        String intermediate = replacePlaceholders(template, localDateTime);

        // 2. 清理非法字符
        return sanitize(intermediate);
    }


}