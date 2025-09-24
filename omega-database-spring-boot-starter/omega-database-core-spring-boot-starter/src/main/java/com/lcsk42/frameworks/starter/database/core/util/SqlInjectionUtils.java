package com.lcsk42.frameworks.starter.database.core.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

/**
 * SQL 注入验证工具类
 */
@Slf4j
public class SqlInjectionUtils {

    /**
     * SQL语法检查正则：符合两个关键字（有先后顺序）才算匹配
     */
    private static final Pattern SQL_SYNTAX_PATTERN = Pattern.compile(
            "(insert|delete|update|select|create|drop|truncate|grant|alter|deny|revoke|call|execute|exec|declare|show|rename|set)"
                    + "\\s+.*(into|from|set|where|table|database|view|index|on|cursor|procedure|trigger|for|password|union|and|or)|(select\\s*\\*\\s*from\\s+)|(and|or)\\s+.*",
            Pattern.CASE_INSENSITIVE);

    /**
     * 使用'、;或注释截断SQL检查正则
     */
    private static final Pattern SQL_COMMENT_PATTERN =
            Pattern.compile("'.*(or|union|--|#|/\\*|;)", Pattern.CASE_INSENSITIVE);

    /**
     * SQL 语法关键字
     */
    private static final String SQL_SYNTAX_KEYWORD =
            "and |exec |peformance_schema|information_schema|extractvalue|updatexml|geohash|gtid_subset|gtid_subtract|insert |select |delete |update |drop |count |chr |mid |master |truncate |char |declare |;|or |+|--";

    /**
     * SQL 函数检查正则
     */
    private static final String[] SQL_FUNCTION_PATTERN =
            new String[] {"chr\\s*\\(", "mid\\s*\\(", " char\\s*\\(", "sleep\\s*\\(", "user\\s*\\(",
                    "show\\s+tables",
                    "user[\\s]*\\([\\s]*\\)", "show\\s+databases", "sleep\\(\\d*\\)",
                    "sleep\\(.*\\)",};

    private static final String MESSAGE_TEMPLATE = "SQL 注入检查: 检查值=>{}<=存在 SQL 注入关键字, 关键字=>{}<=";

    private SqlInjectionUtils() {}

    /**
     * 检查参数是否存在 SQL 注入
     *
     * @param value 检查参数
     * @return true：存在；false：不存在
     */
    public static boolean check(String value) {
        return check(value, null);
    }

    /**
     * 检查参数是否存在 SQL 注入
     *
     * @param value 检查参数
     * @param customKeyword 自定义关键字
     * @return true：存在；false：不存在
     */
    public static boolean check(String value, String customKeyword) {
        if (StringUtils.isBlank(value)) {
            return false;
        }
        // 处理是否包含 SQL 注释字符 || 检查是否包含 SQL 注入敏感字符
        if (SQL_COMMENT_PATTERN.matcher(value).find() || SQL_SYNTAX_PATTERN.matcher(value).find()) {
            log.warn("SQL 注入检查: 检查值=>{}<=存在 SQL 注释字符或 SQL 注入敏感字符", value);
            return true;
        }
        // 转换成小写再进行比较
        value = value.toLowerCase().trim();
        // 检查是否包含 SQL 语法关键字
        if (checkKeyword(value, SQL_SYNTAX_KEYWORD.split("\\|"))) {
            return true;
        }
        // 检查是否包含自定义关键字
        if (StringUtils.isNotBlank(customKeyword)
                && checkKeyword(value, customKeyword.split("\\|"))) {
            return true;
        }
        // 检查是否包含 SQL 注入敏感字符
        for (String pattern : SQL_FUNCTION_PATTERN) {
            if (Pattern.matches(".*" + pattern + ".*", value)) {
                log.warn(MESSAGE_TEMPLATE, value, pattern);
                return true;
            }
        }
        return false;
    }

    /**
     * 检查参数是否存在关键字
     *
     * @param value 检查参数
     * @param keywords 关键字列表
     * @return true：存在；false：不存在
     */
    private static boolean checkKeyword(String value, String[] keywords) {
        for (String keyword : keywords) {
            if (value.contains(keyword)) {
                log.warn(MESSAGE_TEMPLATE, value, keyword);
                return true;
            }
        }
        return false;
    }
}
