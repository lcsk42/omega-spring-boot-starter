package com.lcsk42.frameworks.starter.idempotent.impl.param;

import com.lcsk42.frameworks.starter.common.util.UserContext;
import com.lcsk42.frameworks.starter.convention.exception.ClientException;
import com.lcsk42.frameworks.starter.idempotent.handler.AbstractIdempotentExecuteHandler;
import com.lcsk42.frameworks.starter.idempotent.model.IdempotentContext;
import com.lcsk42.frameworks.starter.idempotent.model.IdempotentParamWrapper;
import com.lcsk42.frameworks.starter.json.jackson.util.JacksonUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.DigestUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

/**
 * 基于方法参数验证请求幂等性
 */
@RequiredArgsConstructor
public class IdempotentParamExecuteHandler extends AbstractIdempotentExecuteHandler
        implements IdempotentParamService {

    private static final Logger log = LoggerFactory.getLogger(IdempotentParamExecuteHandler.class);
    private final RedissonClient redissonClient;

    private final static String LOCK = "lock:param:rest-api";

    @Override
    protected IdempotentParamWrapper buildWrapper(ProceedingJoinPoint joinPoint) {
        String lockKey = String.format("idempotent:context-path:%s:user-id:%s:md5:%s",
                getServletPath(), getCurrentUserId(), calcArgsMD5(joinPoint));
        return IdempotentParamWrapper.builder()
                .lockKey(lockKey)
                .joinPoint(joinPoint)
                .build();
    }

    /**
     * @return 获取当前线程上下文 ServletPath
     */
    private String getServletPath() {
        return Optional.ofNullable(RequestContextHolder.getRequestAttributes())
                .map(it -> (ServletRequestAttributes) it)
                .map(ServletRequestAttributes::getRequest)
                .map(HttpServletRequest::getServletPath)
                .orElse(StringUtils.EMPTY);
    }


    /**
     * @return 当前操作用户 ID
     */
    private Long getCurrentUserId() {
        return Optional.ofNullable(UserContext.getUserId())
                .orElseGet(() -> {
                    log.info("用户ID获取失败");
                    return NumberUtils.LONG_ZERO;
                });
    }

    /**
     * @return joinPoint md5
     */
    private String calcArgsMD5(ProceedingJoinPoint joinPoint) {
        return Optional.ofNullable(joinPoint.getArgs())
                .map(JacksonUtil::toJSON)
                .map(String::getBytes)
                .map(DigestUtils::md5DigestAsHex)
                .map(String::new)
                .orElse(StringUtils.EMPTY);
    }


    /**
     * 尝试加锁
     *
     * @param wrapper 幂等参数包装器
     */
    @Override
    public void handler(IdempotentParamWrapper wrapper) {
        String lockKey = wrapper.getLockKey();
        RLock lock = redissonClient.getLock(lockKey);
        if (!lock.tryLock()) {
            throw new ClientException(wrapper.getIdempotent().message());
        }
        IdempotentContext.put(LOCK, lock);
    }

    /**
     * 后置处理释放锁
     */
    @Override
    public void postProcessing() {
        RLock lock = null;
        try {
            lock = (RLock) IdempotentContext.getKey(LOCK);
        } finally {
            if (lock != null) {
                lock.unlock();
            }
        }
    }
}
