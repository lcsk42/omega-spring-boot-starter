package com.lcsk42.frameworks.starter.database.mybatisplus.model.page;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lcsk42.frameworks.starter.database.core.model.response.BasePageResponse;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * 分页信息
 *
 * @param <V>
 */
public class PageResponse<V> extends BasePageResponse<V> {

    public PageResponse(long total, List<V> records) {
        super(total, records);
    }

    public PageResponse(long current, long size, long total, List<V> records) {
        super(current, size, total, records);
    }

    /**
     * 空分页信息
     *
     * @param <V> 列表数据类型
     * @return 分页信息
     */
    private static <V> PageResponse<V> empty() {
        return new PageResponse<>(0L, List.of());
    }

    /**
     * 基于 MyBatis Plus 分页数据构建分页信息，并将源数据转换为指定类型数据
     *
     * @param page MyBatis Plus 分页数据
     * @param convert 数据处理方法
     * @param <T> 源列表数据类型
     * @param <V> 目标列表数据类型
     * @return 分页信息
     */
    public static <T, V> PageResponse<V> of(IPage<T> page, Function<T, V> convert) {
        if (Objects.isNull(page)) {
            return empty();
        }
        return new PageResponse<>(
                page.getCurrent(),
                page.getSize(),
                page.getSize(),
                page.getRecords().stream()
                        .map(convert)
                        .toList());
    }
}
