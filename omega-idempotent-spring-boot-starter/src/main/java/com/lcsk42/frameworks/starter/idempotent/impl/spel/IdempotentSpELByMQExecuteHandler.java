package com.lcsk42.frameworks.starter.idempotent.impl.spel;

import com.lcsk42.frameworks.starter.cache.redisson.DistributedCache;
import com.lcsk42.frameworks.starter.idempotent.annotation.Idempotent;
import com.lcsk42.frameworks.starter.idempotent.aop.IdempotentAspect;
import com.lcsk42.frameworks.starter.idempotent.enums.IdempotentMQConsumeStatusEnum;
import com.lcsk42.frameworks.starter.idempotent.exception.RepeatConsumptionException;
import com.lcsk42.frameworks.starter.idempotent.handler.AbstractIdempotentExecuteHandler;
import com.lcsk42.frameworks.starter.idempotent.model.IdempotentContext;
import com.lcsk42.frameworks.starter.idempotent.model.IdempotentParamWrapper;
import com.lcsk42.frameworks.starter.idempotent.util.KeyUtil;
import com.lcsk42.frameworks.starter.idempotent.util.LogUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 基于 SpEL 方法验证请求幂等性，适用于 MQ 场景
 */
@RequiredArgsConstructor
public final class IdempotentSpELByMQExecuteHandler extends AbstractIdempotentExecuteHandler
        implements IdempotentSpELService {

    private final static int TIMEOUT = 600;

    private final static String WRAPPER = "wrapper:spel:mq";

    private final static String LUA_SCRIPT_SET_IF_ABSENT_AND_GET_PATH =
            "lua/set_if_absent_and_get.lua";

    private final DistributedCache distributedCache;

    @SneakyThrows
    @Override
    protected IdempotentParamWrapper buildWrapper(ProceedingJoinPoint joinPoint) {
        Idempotent idempotent = IdempotentAspect.getIdempotent(joinPoint);
        String key = KeyUtil.getCacheKey(joinPoint, idempotent);
        return IdempotentParamWrapper.builder().lockKey(key).joinPoint(joinPoint).build();
    }

    @Override
    public void handler(IdempotentParamWrapper wrapper) {
        String uniqueKey = wrapper.getIdempotent().uniqueKeyPrefix() + wrapper.getLockKey();
        String absentAndGet = this.setIfAbsentAndGet(uniqueKey,
                IdempotentMQConsumeStatusEnum.CONSUMING.getStatus(), TIMEOUT, TimeUnit.SECONDS);

        if (Objects.nonNull(absentAndGet)) {
            boolean error = IdempotentMQConsumeStatusEnum.isError(absentAndGet);
            LogUtil.getLog(wrapper.getJoinPoint()).warn("[{}] MQ repeated consumption, {}.",
                    uniqueKey,
                    error ? "Wait for the client to delay consumption" : "Status is completed");
            throw new RepeatConsumptionException(error);
        }
        IdempotentContext.put(WRAPPER, wrapper);
    }

    public String setIfAbsentAndGet(String key, String value, long timeout, TimeUnit timeUnit) {
        DefaultRedisScript<String> redisScript = new DefaultRedisScript<>();
        ClassPathResource resource = new ClassPathResource(LUA_SCRIPT_SET_IF_ABSENT_AND_GET_PATH);
        redisScript.setScriptSource(new ResourceScriptSource(resource));
        redisScript.setResultType(String.class);

        long millis = timeUnit.toMillis(timeout);
        return ((StringRedisTemplate) distributedCache.getInstance()).execute(redisScript,
                List.of(key), value, String.valueOf(millis));
    }

    @Override
    public void exceptionProcessing() {
        IdempotentParamWrapper wrapper = (IdempotentParamWrapper) IdempotentContext.getKey(WRAPPER);
        if (wrapper != null) {
            Idempotent idempotent = wrapper.getIdempotent();
            String uniqueKey = idempotent.uniqueKeyPrefix() + wrapper.getLockKey();
            try {
                distributedCache.delete(uniqueKey);
            } catch (Throwable ex) {
                LogUtil.getLog(wrapper.getJoinPoint())
                        .error("[{}] Failed to del MQ anti-heavy token.", uniqueKey);
            }
        }
    }

    @Override
    public void postProcessing() {
        IdempotentParamWrapper wrapper = (IdempotentParamWrapper) IdempotentContext.getKey(WRAPPER);
        if (wrapper != null) {
            Idempotent idempotent = wrapper.getIdempotent();
            String uniqueKey = idempotent.uniqueKeyPrefix() + wrapper.getLockKey();
            try {

                distributedCache.put(uniqueKey, IdempotentMQConsumeStatusEnum.CONSUMED.getStatus(),
                        Duration.of(idempotent.timeout(), idempotent.unit().toChronoUnit()));
            } catch (Throwable ex) {
                LogUtil.getLog(wrapper.getJoinPoint())
                        .error("[{}] Failed to set MQ anti-heavy token.", uniqueKey);
            }
        }
    }
}
