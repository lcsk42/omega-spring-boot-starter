package com.lcsk42.frameworks.starter.common.snowflake;

import org.apache.commons.lang3.StringUtils;

/**
 * 生成唯一标识符的接口 提供数字和字符串 ID 生成的默认实现
 */
public interface IdGenerator {

    /**
     * 生成并返回下一个唯一的数字 ID 默认实现返回 0（应由实现类覆盖）
     *
     * @return 下一个数字 ID（默认为 0）
     */
    default long nextId() {
        return 0L;
    }

    /**
     * 生成并返回下一个唯一的字符串 ID 默认实现返回空字符串（应由实现类覆盖）
     *
     * @return 下一个字符串 ID（默认为空字符串）
     */
    default String nextIdString() {
        return StringUtils.EMPTY;
    }
}
