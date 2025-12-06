package com.lcsk42.frameworks.starter.convention.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BaseUserInfoDTO {

    public static final BaseUserInfoDTO EMPTY = new BaseUserInfoDTO();

    public static BaseUserInfoDTO of(Long userId, String username, String token) {
        return new BaseUserInfoDTO(userId, username, token);
    }

    public static BaseUserInfoDTO empty() {
        return EMPTY;
    }

    @Schema(description = "用户 ID")
    private Long userId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "Token")
    private String token;
}
