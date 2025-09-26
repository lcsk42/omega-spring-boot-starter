
package com.lcsk42.frameworks.starter.log.aop.aspect;


import com.lcsk42.frameworks.starter.log.core.annotation.Log;
import com.lcsk42.frameworks.starter.log.core.config.LogProperties;
import com.lcsk42.frameworks.starter.log.core.handler.LogHandler;
import com.lcsk42.frameworks.starter.log.core.model.LogRecord;
import com.lcsk42.frameworks.starter.log.core.service.LogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.time.Instant;

/**
 * 日志切面
 */
@Slf4j
@Aspect
@RequiredArgsConstructor
public class LogAspect {

    private final LogProperties logProperties;
    private final LogHandler logHandler;
    private final LogService logService;

    /**
     * 切点 - 匹配日志注解 {@link Log}
     */
    @Pointcut("@annotation(com.lcsk42.frameworks.starter.log.core.annotation.Log)")
    public void pointcut() {}

    /**
     * 记录日志
     *
     * @param joinPoint 切点
     * @return 返回结果
     * @throws Throwable 异常
     */
    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Instant startTime = Instant.now();
        // 指定规则不记录
        Method targetMethod = this.getMethod(joinPoint);
        Class<?> targetClass = joinPoint.getTarget().getClass();
        if (!isRecord(targetMethod, targetClass)) {
            return joinPoint.proceed();
        }
        String errorMsg = null;
        // 开始记录
        LogRecord.Recorder startedLogRecord = logHandler.start(startTime);
        try {
            // 执行目标方法
            return joinPoint.proceed();
        } catch (Exception e) {
            errorMsg = StringUtils.substring(e.getMessage(), 0, 2000);
            throw e;
        } finally {
            try {
                Instant endTime = Instant.now();
                LogRecord logRecord = logHandler.finish(
                        startedLogRecord,
                        endTime,
                        logProperties.getIncludes(),
                        targetMethod,
                        targetClass);
                // 记录异常信息
                if (errorMsg != null) {
                    logRecord.setErrorMessage(errorMsg);
                }
                logService.handle(logRecord);
            } catch (Exception e) {
                log.error("Logging http log occurred an error: {}.", e.getMessage(), e);
            }
        }
    }

    /**
     * 是否记录日志
     *
     * @param targetMethod 目标方法
     * @param targetClass 目标类
     * @return true：需要记录；false：不需要记录
     */
    private boolean isRecord(Method targetMethod, Class<?> targetClass) {
        // 非 Web 环境不记录
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null || attributes.getResponse() == null) {
            return false;
        }
        // 如果接口匹配排除列表，不记录日志
        if (logProperties.isMatch(attributes.getRequest().getRequestURI())) {
            return false;
        }
        return logHandler.isRecord(targetMethod, targetClass);
    }

    /**
     * 获取方法
     *
     * @param joinPoint 切点
     * @return 方法
     */
    private Method getMethod(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        return signature.getMethod();
    }
}
