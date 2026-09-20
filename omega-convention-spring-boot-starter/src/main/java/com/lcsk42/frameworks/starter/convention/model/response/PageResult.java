package com.lcsk42.frameworks.starter.convention.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<V> {
    /**
     * 当前页码
     */
    private long current;

    /**
     * 页面大小
     */
    @Builder.Default
    private long size = 10L;

    /**
     * 总数量
     */
    @Builder.Default
    private long total = 0L;

    /**
     * 查询出的记录
     */
    @SuppressWarnings("squid:S1948")
    @Builder.Default
    private List<V> records = List.of();

    public PageResult(long total, List<V> records) {
        this.total = total;
        this.records = records;
    }

    public PageResult(long current, long size) {
        this(current, size, 0);
    }

    public PageResult(long current, long size, long total) {
        if (current > 1) {
            this.current = current;
        }
        this.size = size;
        this.total = total;
    }
}
