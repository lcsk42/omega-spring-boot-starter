package com.lcsk42.frameworks.starter.convention.model.query;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.hibernate.validator.constraints.Range;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@Schema(description = "分页查询参数")
public class PageQuery {
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
}
