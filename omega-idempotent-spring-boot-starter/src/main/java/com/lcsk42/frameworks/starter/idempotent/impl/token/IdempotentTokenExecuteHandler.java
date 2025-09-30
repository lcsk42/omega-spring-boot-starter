package com.lcsk42.frameworks.starter.idempotent.impl.token;

import com.lcsk42.frameworks.starter.cache.redisson.DistributedCache;
import com.lcsk42.frameworks.starter.common.util.IdUtil;
import com.lcsk42.frameworks.starter.convention.exception.ClientException;
import com.lcsk42.frameworks.starter.core.constant.HttpHeaderConstant;
import com.lcsk42.frameworks.starter.idempotent.exception.IdempotentErrorCode;
import com.lcsk42.frameworks.starter.idempotent.handler.AbstractIdempotentExecuteHandler;
import com.lcsk42.frameworks.starter.idempotent.model.IdempotentParamWrapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Duration;
import java.util.Optional;

/**
 * 基于 Token 验证请求幂等性, 通常应用于 RestAPI 方法
 */
@RequiredArgsConstructor
public class IdempotentTokenExecuteHandler extends AbstractIdempotentExecuteHandler
        implements IdempotentTokenService {
    private final DistributedCache distributedCache;

    private static final String TOKEN_PREFIX_KEY = "idempotent:token:";
    private static final long TOKEN_EXPIRED_TIME = 60 * 1_000L;

    @Override
    protected IdempotentParamWrapper buildWrapper(ProceedingJoinPoint joinPoint) {
        return new IdempotentParamWrapper();
    }

    @Override
    public String createToken(Long expiredMillis) {
        String token = TOKEN_PREFIX_KEY + IdUtil.generateStandardUuid();
        distributedCache.put(token, StringUtils.EMPTY,
                Duration.ofMillis(Optional.ofNullable(expiredMillis).orElse(TOKEN_EXPIRED_TIME)));
        return token;
    }

    @Override
    public void handler(IdempotentParamWrapper wrapper) {
        HttpServletRequest request =
                ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes()))
                        .getRequest();
        String token = request.getHeader(HttpHeaderConstant.IDEMPOTENT_TOKEN);
        if (StringUtils.isBlank(token)) {
            token = request.getParameter(HttpHeaderConstant.IDEMPOTENT_TOKEN_PARAM);
            if (StringUtils.isBlank(token)) {
                throw new ClientException(IdempotentErrorCode.IDEMPOTENT_TOKEN_NULL_ERROR);
            }
        }
        boolean tokenDelFlag = distributedCache.delete(token);
        if (!tokenDelFlag) {
            String errMsg = StringUtils.isNotBlank(wrapper.getIdempotent().message())
                    ? wrapper.getIdempotent().message()
                    : IdempotentErrorCode.IDEMPOTENT_TOKEN_DELETE_ERROR.getMessage();
            throw new ClientException(errMsg, IdempotentErrorCode.IDEMPOTENT_TOKEN_DELETE_ERROR);
        }
    }
}
