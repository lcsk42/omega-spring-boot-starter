package com.lcsk42.frameworks.starter.common.word;

import java.util.Arrays;

public interface WordList {


    /**
     * 获取单词表
     */
    String[] getWordList();

    /**
     * 获取单词列表中的一个单词
     *
     * @param index index 单词在单词列表中的索引 [0..2047]（含边界值）
     * @return 列表中的该单词
     */
    default String getWord(final int index) {
        return getWordList()[index];
    }

    /**
     * 返回单词索引，找不到返回 -1
     */
    default int getIndex(final String word) {
        if (word == null) {
            return -1;
        }
        int index = Arrays.binarySearch(getWordList(), word);
        return index >= 0 ? index : -1;
    }
}
