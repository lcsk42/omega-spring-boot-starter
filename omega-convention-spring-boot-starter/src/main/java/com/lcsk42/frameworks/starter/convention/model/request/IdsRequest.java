package com.lcsk42.frameworks.starter.convention.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
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
    private List<Long> ids;
}
