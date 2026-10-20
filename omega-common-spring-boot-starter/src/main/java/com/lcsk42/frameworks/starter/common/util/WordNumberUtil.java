package com.lcsk42.frameworks.starter.common.util;

import com.google.common.base.Preconditions;
import com.lcsk42.frameworks.starter.common.word.WordList;
import com.lcsk42.frameworks.starter.core.constant.StringConstant;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 单词数字编解码工具类。
 * <p>
 * 用于在「非负 long 数值」与「由单词组成的字符串」之间进行双向转换，
 * 每个单词代表 2048 进制中的一位（词表大小需为 2048）。
 * </p>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class WordNumberUtil {

    /**
     * 单词进制基数：每个单词位可表示 0 ~ 2047。
     */
    private static final long RADIX = 2048;

    /**
     * 将非负 long 数值编码为单词序列字符串（使用默认空格分隔）。
     *
     * @param wordList 词表
     * @param number 待编码的非负数值
     * @return 单词序列字符串
     */
    public static String encode(WordList wordList, long number) {
        return encode(wordList, number, StringConstant.DASHED);
    }

    /**
     * 将非负 long 数值编码为单词序列字符串。
     *
     * @param wordList 词表
     * @param number 待编码的非负数值
     * @param separator 单词之间的分隔符
     * @return 单词序列字符串
     */
    public static String encode(WordList wordList, long number, String separator) {
        Preconditions.checkNotNull(wordList, "词表不能为空");
        Preconditions.checkArgument(StringUtils.isNotEmpty(separator), "分隔符不能为空");
        Preconditions.checkArgument(number >= 0, "数值必须大于等于 0");

        // 0 是特殊情况，直接返回词表第一个单词
        if (number == 0) {
            return wordList.getWord(0);
        }

        List<String> result = new ArrayList<>();
        long value = number;
        // 按 2048 进制逐位取模，得到的是低位在前的顺序（小端序）
        while (value != 0) {
            int digit = (int) (value % RADIX);
            value /= RADIX;
            result.add(wordList.getWord(digit));
        }

        // 小端序 → 大端序，保证高位单词在前，结果更直观
        Collections.reverse(result);

        return String.join(separator, result);
    }

    /**
     * 将单词序列字符串解码为非负 long 数值（使用默认空格分隔）。
     *
     * @param wordList 词表
     * @param word 单词序列字符串
     * @return 解码后的数值
     */
    public static long decode(WordList wordList, String word) {
        return decode(wordList, word, StringConstant.DASHED);
    }

    /**
     * 将单词序列字符串解码为非负 long 数值。
     *
     * @param wordList 词表
     * @param word 单词序列字符串
     * @param separator 单词之间的分隔符
     * @return 解码后的数值
     */
    public static long decode(WordList wordList, String word, String separator) {
        Preconditions.checkNotNull(wordList, "词表不能为空");
        Preconditions.checkArgument(StringUtils.isNotEmpty(separator), "分隔符不能为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(word), "转换异常：待解码字符串不能为空");

        long result = 0L;
        for (String item : word.split(separator, -1)) {
            int idx = wordList.getIndex(item);
            if (idx < 0) {
                throw new IllegalArgumentException("单词转数值失败，未知单词：" + item);
            }
            // 使用精确运算，数值超出 long 范围时抛出中文异常
            try {
                result = Math.addExact(Math.multiplyExact(result, RADIX), idx);
            } catch (ArithmeticException e) {
                throw new IllegalArgumentException("单词转数值失败，结果超出 long 取值范围：" + word, e);
            }
        }
        return result;
    }
}
