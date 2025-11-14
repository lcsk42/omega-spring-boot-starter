package com.lcsk42.frameworks.starter.security.mask.strategy;


import org.apache.commons.lang3.StringUtils;

/**
 * 数据脱敏处理策略接口
 */
public interface MaskStrategy {
    /**
     * 对原始字符串执行脱敏处理
     *
     * @param originalString 原始输入字符串
     * @param maskingChar 用于替换的脱敏字符，默认为'*'
     * @param leftVisibleLen 左侧保留的可见字符长度
     * @param rightVisibleLen 右侧保留的可见字符长度
     * @return 脱敏处理后的字符串
     * @throws IllegalArgumentException 如果输入参数不合法
     */
    String mask(String originalString, char maskingChar, int leftVisibleLen, int rightVisibleLen);

    /**
     * 简化版脱敏方法，使用默认的脱敏字符'*'
     */
    default String mask(String originalString, int leftVisibleLen, int rightVisibleLen) {
        return mask(originalString, '*', leftVisibleLen, rightVisibleLen);
    }

    default String handler(String originalString, char maskingChar, int leftVisibleLen,
            int rightVisibleLen) {
        // 参数校验
        if (StringUtils.isEmpty(originalString)) {
            return originalString;
        }

        if (leftVisibleLen < 0 || rightVisibleLen < 0) {
            throw new IllegalArgumentException("保留长度不能为负数");
        }

        int totalVisible = leftVisibleLen + rightVisibleLen;
        if (totalVisible >= originalString.length()) {
            return originalString; // 如果要显示的比原字符串长，直接返回原字符串
        }
        // 获取左右保留部分
        String leftPart = StringUtils.left(originalString, leftVisibleLen);
        String rightPart = StringUtils.right(originalString, rightVisibleLen);

        // 计算中间需要脱敏的部分长度
        int maskingLength = originalString.length() - totalVisible;
        String maskingPart = StringUtils.repeat(maskingChar, maskingLength);

        // 拼接结果
        return leftPart + maskingPart + rightPart;
    }
}
