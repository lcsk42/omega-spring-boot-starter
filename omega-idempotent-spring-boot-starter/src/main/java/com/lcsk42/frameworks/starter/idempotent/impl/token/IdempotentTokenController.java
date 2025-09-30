package com.lcsk42.frameworks.starter.idempotent.impl.token;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 基于 Token 验证请求幂等性控制器
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/idempotent")
@Tag(name = "幂等 Token 生成器")
public class IdempotentTokenController {

    private final IdempotentTokenService idempotentTokenService;

    /**
     * 请求申请Token
     */
    @GetMapping("/token")
    @Operation(summary = "幂等校验 Token 生成")
    public String createToken(@RequestParam(required = false) Long expiredMillis) {
        return idempotentTokenService.createToken(expiredMillis);
    }
}
