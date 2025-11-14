package com.lcsk42.frameworks.starter.security.mask.enums;

import com.lcsk42.frameworks.starter.core.constant.StringConstant;
import com.lcsk42.frameworks.starter.security.mask.strategy.MaskStrategy;
import org.apache.commons.lang3.StringUtils;

/**
 * 脱敏类型
 */
public enum MaskType implements MaskStrategy {
    /**
     * 自定义脱敏
     */
    CUSTOM {
        @Override
        public String mask(String originalString, char maskingChar, int leftVisibleLen,
                int rightVisibleLen) {
            return handleMasking(originalString, maskingChar, leftVisibleLen, rightVisibleLen);
        }
    },

    /**
     * 手机号码 - 保留前3位和后4位，例如：135****2210
     */
    MOBILE_PHONE {
        @Override
        public String mask(String originalString, char maskingChar, int leftVisibleLen,
                int rightVisibleLen) {
            return handleMasking(originalString, maskingChar, 3, 4);
        }
    },

    /**
     * 邮箱 - 保留@前1位和域名，例如：a***@example.com
     */
    EMAIL {
        @Override
        public String mask(String originalString, char maskingChar, int leftVisibleLen,
                int rightVisibleLen) {
            if (StringUtils.isEmpty(originalString) || !originalString.contains("@")) {
                return originalString;
            }

            String[] parts = originalString.split("@");
            String localPart = handleMasking(parts[0], maskingChar, 1, 0);
            return localPart + "@" + parts[1];
        }
    },

    /**
     * 身份证号
     * <p>
     * 保留前 1 位和后 2 位
     * </p>
     */
    ID_CARD {
        @Override
        public String mask(String originalString, char maskingChar, int leftVisibleLen,
                int rightVisibleLen) {
            return handleMasking(originalString, maskingChar, 1, 2);
        }
    },

    /**
     * 中国大陆车牌（包含普通车辆、新能源车辆）
     * <p>
     * 例如：苏D40000 => 苏D4***0
     * </p>
     */
    CAR_LICENSE {
        @Override
        public String mask(String originalString, char maskingChar, int leftVisibleLen,
                int rightVisibleLen) {
            return handleMasking(originalString, maskingChar, 3, 1);
        }
    },

    /**
     * 银行卡
     * <p>
     * 由于银行卡号长度不定，所以只保留前 4 位，后面保留的位数根据卡号决定展示 1-4 位
     * <ul>
     * <li>1234 2222 3333 4444 6789 9 => 1234 **** **** **** **** 9</li>
     * <li>1234 2222 3333 4444 6789 91 => 1234 **** **** **** **** 91</li>
     * <li>1234 2222 3333 4444 678 => 1234 **** **** **** 678</li>
     * <li>1234 2222 3333 4444 6789 => 1234 **** **** **** 6789</li>
     * </ul>
     * </p>
     */
    BANK_CARD {
        @Override
        public String mask(String originalString, char maskingChar, int leftVisibleLen,
                int rightVisibleLen) {
            String cleanStr = StringUtils.deleteWhitespace(originalString);
            rightVisibleLen = cleanStr.length() % 4;
            if (rightVisibleLen == 0) {
                rightVisibleLen = 4;
            }
            String handleMasking = handleMasking(cleanStr, maskingChar, 4, rightVisibleLen);
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < handleMasking.length(); i++) {
                if (i > 0 && i % 4 == 0) {
                    sb.append(StringConstant.SPACE);
                }
                sb.append(handleMasking.charAt(i));
            }

            return sb.toString();
        }
    },

    /**
     * 中文名
     * <p>
     * 只保留第 1 个汉字，例如：李**
     * </p>
     */
    CHINESE_NAME {
        @Override
        public String mask(String originalString, char maskingChar, int leftVisibleLen,
                int rightVisibleLen) {
            return handleMasking(originalString, maskingChar, 1, 0);
        }
    },

    /**
     * 密码
     * <p>
     * 密码的全部字符都使用脱敏符号代替，例如：******
     * </p>
     */
    PASSWORD {
        @Override
        public String mask(String str, char character, int left, int right) {
            return "******";
        }
    },
    ;


    /**
     * 通用的脱敏处理方法
     */
    protected String handleMasking(String originalString, char maskingChar, int leftVisibleLen,
            int rightVisibleLen) {
        // 参数校验
        if (StringUtils.isEmpty(originalString)) {
            return originalString;
        }
        if (leftVisibleLen < 0 || rightVisibleLen < 0) {
            throw new IllegalArgumentException(
                    "保留长度不能为负数: left=" + leftVisibleLen + ", right=" + rightVisibleLen);
        }
        int totalVisible = leftVisibleLen + rightVisibleLen;
        if (totalVisible >= originalString.length()) {
            return originalString;
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
