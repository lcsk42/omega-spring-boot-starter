package com.lcsk42.frameworks.starter.database.core.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BasePageResponse<V> {
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

    public BasePageResponse(long total, List<V> records) {
        this.total = total;
        this.records = records;
    }

    public static <V> BasePageResponse<V> of(long current, long size) {
        return BasePageResponse.<V>builder()
                .current(current)
                .size(size)
                .build();
    }

    public static <V> BasePageResponse<V> of(long current, long size, long total, List<V> records) {
        return BasePageResponse.<V>builder()
                .current(current)
                .size(size)
                .total(total)
                .records(records)
                .build();
    }


    public BasePageResponse(long current, long size) {
        this(current, size, 0);
    }

    public BasePageResponse(long current, long size, long total) {
        if (current > 1) {
            this.current = current;
        }
        this.size = size;
        this.total = total;
    }

    public <R> BasePageResponse<R> convert(Function<? super V, ? extends R> mapper) {
        List<? extends R> mapped = this.getRecords().stream()
                .map(mapper)
                .toList();
        List<R> collect = new ArrayList<>(mapped);
        return BasePageResponse.<R>builder()
                .current(this.current)
                .size(this.size)
                .total(this.total)
                .records(collect)
                .build();
    }
}
