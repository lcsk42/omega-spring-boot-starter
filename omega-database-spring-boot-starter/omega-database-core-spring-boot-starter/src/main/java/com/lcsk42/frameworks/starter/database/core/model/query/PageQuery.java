package com.lcsk42.frameworks.starter.database.core.model.query;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.hibernate.validator.constraints.Range;
import org.springframework.data.domain.Sort;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@Schema(description = "分页查询参数")
public class PageQuery extends SortQuery {
    /**
     * 默认页码：1
     */
    private static final long DEFAULT_PAGE = 1L;

    /**
     * 默认每页条数：10
     */
    private static final long DEFAULT_SIZE = 10L;

    /**
     * 页码
     */
    @Schema(description = "页码", example = "1")
    @Min(value = 1, message = "页码最小值为 {value}")
    private Long current = DEFAULT_PAGE;

    /**
     * 每页条数
     */
    @Schema(description = "每页条数", example = "10")
    @Range(min = 1, max = 1000, message = "每页条数（取值范围 {min}-{max}）")
    private Long size = DEFAULT_SIZE;

    /**
     * 构造方法
     *
     * <p>
     * 示例：{@code new PageQuery(1, 10, "createTime,desc", "name,asc")}
     * </p>
     *
     * @param current 页码
     * @param size 每页条数
     * @param sort 排序
     */
    public PageQuery(Long current, Long size, String... sort) {
        super(sort);
        this.current = current;
        this.size = size;
    }

    /**
     * 构造方法
     *
     * <p>
     * 示例：{@code new PageQuery("createTime,desc", "name,asc")}
     * </p>
     *
     * @param sort 排序
     */
    public PageQuery(String... sort) {
        super(sort);
    }

    /**
     * 构造方法
     *
     * <p>
     * 示例：{@code new PageQuery("createTime", Sort.Direction.DESC)}
     * </p>
     *
     * @param field 字段
     * @param direction 排序方向
     */
    public PageQuery(String field, Sort.Direction direction) {
        super(field, direction);
    }

    /**
     * 构造方法
     *
     * <p>
     * 示例：{@code new PageQuery(Sort.by(Sort.Direction.DESC, "createTime"))}
     * </p>
     *
     * @param sort 排序
     */
    public PageQuery(Sort sort) {
        super(sort);
    }

    /**
     * 构造方法
     *
     * <p>
     * 示例：{@code new PageQuery(1, 10, "createTime", Sort.Direction.DESC)}
     * </p>
     *
     * @param current 页码
     * @param size 每页条数
     * @param field 字段
     * @param direction 排序方向
     */
    public PageQuery(Long current, Long size, String field, Sort.Direction direction) {
        super(field, direction);
        this.current = current;
        this.size = size;
    }

    /**
     * 构造方法
     *
     * <p>
     * 示例：{@code new PageQuery(1, 10, Sort.by(Sort.Direction.DESC, "createTime"))}
     * </p>
     *
     * @param current 页码
     * @param size 每页条数
     * @param sort 排序
     */
    public PageQuery(Long current, Long size, Sort sort) {
        super(sort);
        this.current = current;
        this.size = size;
    }
}
