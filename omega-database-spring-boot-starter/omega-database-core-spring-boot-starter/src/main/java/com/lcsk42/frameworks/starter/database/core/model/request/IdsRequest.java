package com.lcsk42.frameworks.starter.database.core.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * ID 列表请求参数
 */
@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Ids 接受参数")
public class IdsRequest {
    /**
     * ID
     */
    @Schema(description = "Ids", example = "[1,2]")
    @NotEmpty(message = "Id 不能为空")
    private List<Long> ids;
}
