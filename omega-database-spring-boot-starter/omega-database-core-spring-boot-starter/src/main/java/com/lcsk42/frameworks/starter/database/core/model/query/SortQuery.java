package com.lcsk42.frameworks.starter.database.core.model.query;

import com.lcsk42.frameworks.starter.core.constant.StringConstant;
import com.lcsk42.frameworks.starter.database.core.util.SqlInjectionUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 排序查询条件
 */
@Getter
@Setter
@Schema(description = "排序查询条件")
public class SortQuery {
    /**
     * 排序条件
     */
    @Schema(description = "排序条件", example = "createTime,desc")
    private String[] sort;

    /**
     * 构造方法
     *
     * <p>
     * 示例：{@code new SortQuery("createTime,desc", "name,asc")}
     * </p>
     *
     * @param sort 排序条件
     */
    public SortQuery(String... sort) {
        this.sort = sort;
    }

    /**
     * 构造方法
     *
     * <p>
     * 示例：{@code new SortQuery("createTime", Sort.Direction.DESC)}
     * </p>
     *
     * @param field 字段
     * @param direction 排序方向
     */
    public SortQuery(String field, Sort.Direction direction) {
        this(field + StringConstant.COMMA + direction.name().toLowerCase());
    }

    /**
     * 构造方法
     *
     * <p>
     * 示例：{@code new SortQuery(Sort.by(Sort.Order.desc("createTime")))}
     * </p>
     */
    public SortQuery(Sort sort) {
        if (sort == null || sort.isUnsorted()) {
            this.sort = null;
            return;
        }
        this.sort = sort.stream()
                .map(order -> order.getProperty() + StringConstant.COMMA
                        + order.getDirection().name().toLowerCase())
                .toArray(String[]::new);
    }

    /**
     * 获取排序条件
     *
     * @param field 字段
     * @param direction 排序方向
     * @return 排序条件
     */
    private Sort.Order getOrder(String field, String direction) {
        Validate.validState(!SqlInjectionUtils.check(field), "排序字段包含无效字符");
        return new Sort.Order(Sort.Direction.valueOf(direction.toUpperCase()), field);
    }

    /**
     * 解析排序条件为 Spring 分页排序实体
     *
     * @return Spring 分页排序实体
     */
    public Sort getSort() {
        if (ArrayUtils.isEmpty(sort)) {
            return Sort.unsorted();
        }
        Validate.validState(sort.length > 1, "排序条件无效");
        List<Sort.Order> orders = new ArrayList<>(sort.length);
        if (StringUtils.contains(sort[0], StringConstant.COMMA)) {
            // e.g "sort=createTime,desc&sort=name,asc"
            for (String s : sort) {
                List<String> sortList = Arrays.stream(StringUtils.split(s, StringConstant.COMMA))
                        .map(String::trim)
                        .toList();
                orders.add(this.getOrder(sortList.get(0), sortList.get(1)));
            }
        } else {
            // e.g "sort=createTime,desc"
            orders.add(this.getOrder(sort[0], sort[1]));
        }
        return Sort.by(orders);
    }
}
