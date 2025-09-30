package com.lcsk42.frameworks.starter.idempotent.model;

import com.lcsk42.frameworks.starter.idempotent.annotation.Idempotent;
import com.lcsk42.frameworks.starter.idempotent.enums.IdempotentTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.aspectj.lang.ProceedingJoinPoint;

/**
 * 幂等参数包装
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public final class IdempotentParamWrapper {

    /**
     * 幂等注解
     */
    private Idempotent idempotent;

    /**
     * AOP 处理连接点
     */
    private ProceedingJoinPoint joinPoint;

    /**
     * 锁标识，{@link IdempotentTypeEnum#PARAM}
     */
    private String lockKey;
}
