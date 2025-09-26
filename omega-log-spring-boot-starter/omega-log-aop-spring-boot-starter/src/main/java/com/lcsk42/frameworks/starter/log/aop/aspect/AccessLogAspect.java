
package com.lcsk42.frameworks.starter.log.aop.aspect;

import com.lcsk42.frameworks.starter.log.core.config.LogProperties;
import com.lcsk42.frameworks.starter.log.core.handler.LogHandler;
import com.lcsk42.frameworks.starter.log.core.model.AccessLogContext;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Instant;

/**
 * 访问日志切面
 */
@Aspect
@RequiredArgsConstructor
public class AccessLogAspect {

    private final LogProperties logProperties;
    private final LogHandler logHandler;


    /**
     * 切点 - 匹配所有控制器层的 GET 请求方法
     */
    @Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping)")
    public void pointcut() {}

    /**
     * 切点 - 匹配所有控制器层的 GET 请求方法
     */
    @Pointcut("@annotation(org.springframework.web.bind.annotation.GetMapping)")
    public void pointcutGet() {}

    /**
     * 切点 - 匹配所有控制器层的 POST 请求方法
     */
    @Pointcut("@annotation(org.springframework.web.bind.annotation.PostMapping)")
    public void pointcutPost() {}

    /**
     * 切点 - 匹配所有控制器层的 PUT 请求方法
     */
    @Pointcut("@annotation(org.springframework.web.bind.annotation.PutMapping)")
    public void pointcutPut() {}

    /**
     * 切点 - 匹配所有控制器层的 DELETE 请求方法
     */
    @Pointcut("@annotation(org.springframework.web.bind.annotation.DeleteMapping)")
    public void pointcutDelete() {}

    /**
     * 切点 - 匹配所有控制器层的 PATCH 请求方法
     */
    @Pointcut("@annotation(org.springframework.web.bind.annotation.PatchMapping)")
    public void pointcutPatch() {}

    /**
     * 打印访问日志
     *
     * @param joinPoint 切点
     * @return 返回结果
     * @throws Throwable 异常
     */
    @Around("pointcut() || pointcutGet() || pointcutPost() || pointcutPut() || pointcutDelete() || pointcutPatch()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Instant startTime = Instant.now();
        // 非 Web 环境不记录
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return joinPoint.proceed();
        }
        try {
            // 开始访问日志记录
            logHandler.accessLogStart(AccessLogContext.builder()
                    .startTime(startTime)
                    .properties(logProperties)
                    .build());
            return joinPoint.proceed();
        } finally {
            Instant endTime = Instant.now();
            logHandler.accessLogFinish(AccessLogContext.builder().endTime(endTime).build());
        }
    }
}
