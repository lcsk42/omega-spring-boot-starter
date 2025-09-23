package com.lcsk42.frameworks.starter.mq.rocket.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.regex.Pattern;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RocketMQNamingUtils {

    // 基础命名正则表达式
    private static final String BASE_PATTERN = "^[a-z]+([-_][a-z]+)*$";
    private static final Pattern BASE_PATTERN_COMPILED = Pattern.compile(BASE_PATTERN);

    // 完整命名正则表达式
    private static final String FULL_PATTERN = "^[a-z]+([-][a-z]+)*_[a-z]+([-][a-z]+)*_[a-z]+([-][a-z]+)*_(topic|tag|pg|cg)$";
    private static final Pattern FULL_PATTERN_COMPILED = Pattern.compile(FULL_PATTERN);

    // 后缀常量
    private static final String TOPIC_SUFFIX = "topic";
    private static final String TAG_SUFFIX = "tag";
    private static final String PRODUCER_GROUP_SUFFIX = "pg";
    private static final String CONSUMER_GROUP_SUFFIX = "cg";

    /**
     * 验证基础名称是否符合规范（业务线或项目名）
     *
     * @param name 要验证的名称
     * @return 是否符合规范
     */
    public static boolean isValidBaseName(String name) {
        if (name == null || name.isEmpty()) {
            return false;
        }
        return BASE_PATTERN_COMPILED.matcher(name).matches();
    }

    /**
     * 验证完整名称是否符合规范
     *
     * @param name 要验证的名称
     * @return 是否符合规范
     */
    public static boolean isValidFullName(String name) {
        if (name == null || name.isEmpty()) {
            return false;
        }
        return FULL_PATTERN_COMPILED.matcher(name).matches();
    }

    /**
     * 生成 Topic 名称
     *
     * @param businessLine 业务线
     * @param projectName  项目名
     * @return 符合规范的 Topic 名称
     * @throws IllegalArgumentException 如果参数不符合规范
     */
    public static String buildTopicName(String businessLine, String projectName) {
        validateBaseName(businessLine, "业务线");
        validateBaseName(projectName, "项目名");
        return String.format("%s_%s_%s", businessLine, projectName, TOPIC_SUFFIX);
    }

    /**
     * 生成 Tag 名称
     *
     * @param businessLine 业务线
     * @param projectName  项目名
     * @param businessName 业务名
     * @return 符合规范的 Tag 名称
     * @throws IllegalArgumentException 如果参数不符合规范
     */
    public static String buildTagName(String businessLine, String projectName, String businessName) {
        validateBaseName(businessLine, "业务线");
        validateBaseName(projectName, "项目名");
        validateBaseName(businessName, "业务名");
        return String.format("%s_%s_%s_%s", businessLine, projectName, businessName, TAG_SUFFIX);
    }

    /**
     * 生成生产者组名称
     *
     * @param businessLine 业务线
     * @param projectName  项目名
     * @param businessName 业务名
     * @return 符合规范的生产者组名称
     * @throws IllegalArgumentException 如果参数不符合规范
     */
    public static String buildProducerGroupName(String businessLine, String projectName, String businessName) {
        validateBaseName(businessLine, "业务线");
        validateBaseName(projectName, "项目名");
        validateBaseName(businessName, "业务名");
        return String.format("%s_%s_%s_%s", businessLine, projectName, businessName, PRODUCER_GROUP_SUFFIX);
    }

    /**
     * 生成消费者组名称
     *
     * @param businessLine 业务线
     * @param projectName  项目名
     * @param businessName 业务名
     * @return 符合规范的消费者组名称
     * @throws IllegalArgumentException 如果参数不符合规范
     */
    public static String buildConsumerGroupName(String businessLine, String projectName, String businessName) {
        validateBaseName(businessLine, "业务线");
        validateBaseName(projectName, "项目名");
        validateBaseName(businessName, "业务名");
        return String.format("%s_%s_%s_%s", businessLine, projectName, businessName, CONSUMER_GROUP_SUFFIX);
    }

    /**
     * 验证基础名称并抛出异常
     *
     * @param name      名称
     * @param fieldName 字段名（用于错误信息）
     * @throws IllegalArgumentException 如果名称不符合规范
     */
    private static void validateBaseName(String name, String fieldName) {
        if (!isValidBaseName(name)) {
            throw new IllegalArgumentException(
                    String.format("%s '%s' 不符合规范，只能包含小写字母和连字符(-)，且不能以连字符开头或结尾",
                            fieldName, name));
        }
    }
}
