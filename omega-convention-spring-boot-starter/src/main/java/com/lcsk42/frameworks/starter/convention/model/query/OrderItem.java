package com.lcsk42.frameworks.starter.convention.model.query;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Sort;

@Getter
@Setter
@Schema(description = "排序字段")
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    @Schema(description = "字段名称", example = "createTime")
    private String column;

    @Schema(description = "生序还是降序")
    private Sort.Direction direction;
}
