package com.lcsk42.frameworks.starter.ratelimiter.aop;

import com.lcsk42.frameworks.starter.cache.core.util.CacheUtil;
import com.lcsk42.frameworks.starter.common.util.ExpressionUtils;
import com.lcsk42.frameworks.starter.common.util.NetworkUtil;
import com.lcsk42.frameworks.starter.ratelimiter.annotation.RateLimiter;
import com.lcsk42.frameworks.starter.ratelimiter.annotation.RateLimiters;
import com.lcsk42.frameworks.starter.ratelimiter.config.RateLimiterConfiguration;
import com.lcsk42.frameworks.starter.ratelimiter.enums.LimitType;
import com.lcsk42.frameworks.starter.ratelimiter.exception.RateLimiterErrorCode;
import com.lcsk42.frameworks.starter.ratelimiter.exception.RateLimiterException;
import com.lcsk42.frameworks.starter.ratelimiter.generator.RateLimiterNameGenerator;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateLimiterConfig;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 限流器切面
 */
@Aspect
@RequiredArgsConstructor
public class RateLimiterAspect {
    private static final ConcurrentHashMap<String, RRateLimiter> RATE_LIMITER_CACHE =
            new ConcurrentHashMap<>();

    private final RateLimiterConfiguration configuration;
    private final RateLimiterNameGenerator nameGenerator;
    private final RedissonClient redissonClient;


    /**
     * 单个限流注解切点
     */
    @Pointcut("@annotation(com.lcsk42.frameworks.starter.ratelimiter.annotation.RateLimiter)")
    public void rateLimiterPointCut() {
    }

    /**
     * 多个限流注解切点
     */
    @Pointcut("@annotation(com.lcsk42.frameworks.starter.ratelimiter.annotation.RateLimiters)")
    public void rateLimitersPointCut() {
    }

    /**
     * 单限流场景
     *
     * @param joinPoint   切点
     * @param rateLimiter 限流注解
     * @return 目标方法的执行结果
     * @throws Throwable /
     */
    @Around("@annotation(rateLimiter)")
    public Object aroundRateLimiter(ProceedingJoinPoint joinPoint, RateLimiter rateLimiter)
            throws Throwable {
        if (isRateLimited(joinPoint, rateLimiter)) {
            throw new RateLimiterException(rateLimiter.message());
        }
        return joinPoint.proceed();
    }

    /**
     * 多限流场景
     *
     * @param joinPoint    切点
     * @param rateLimiters 限流组注解
     * @return 目标方法的执行结果
     * @throws Throwable /
     */
    @Around("@annotation(rateLimiters)")
    public Object aroundRateLimiters(ProceedingJoinPoint joinPoint, RateLimiters rateLimiters)
            throws Throwable {
        for (RateLimiter rateLimiter : rateLimiters.value()) {
            if (isRateLimited(joinPoint, rateLimiter)) {
                throw new RateLimiterException(rateLimiter.message());
            }
        }
        return joinPoint.proceed();
    }

    /**
     * 是否需要限流
     *
     * @param joinPoint   切点
     * @param rateLimiter 限流注解
     * @return true: 需要限流；false：不需要限流
     */
    private boolean isRateLimited(ProceedingJoinPoint joinPoint, RateLimiter rateLimiter) {
        try {
            String cacheKey = this.getCacheKey(joinPoint, rateLimiter);
            RRateLimiter rRateLimiter = RATE_LIMITER_CACHE.computeIfAbsent(
                    cacheKey, key -> redissonClient.getRateLimiter(cacheKey));
            // 限流器配置
            RateType rateType = rateLimiter.type() == LimitType.CLUSTER ? RateType.PER_CLIENT
                    : RateType.OVERALL;
            int rate = rateLimiter.rate();
            Duration rateInterval =
                    Duration.ofMillis(rateLimiter.unit().toMillis(rateLimiter.interval()));
            // 判断是否需要更新限流器
            if (this.shouldUpdate(rRateLimiter, rateType, rate, rateInterval)) {
                // 更新限流器
                rRateLimiter.setRate(rateType, rate, rateInterval);
            }
            // 尝试获取令牌
            return !rRateLimiter.tryAcquire();
        } catch (Exception e) {
            throw RateLimiterErrorCode.RATE_LIMITER_EXCEPTION.toException();
        }
    }

    /**
     * 获取缓存 Key
     *
     * @param joinPoint   切点
     * @param rateLimiter 限流注解
     * @return 缓存 Key
     */
    private String getCacheKey(JoinPoint joinPoint, RateLimiter rateLimiter) {
        Object target = joinPoint.getTarget();
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        Object[] args = joinPoint.getArgs();
        // 获取名称
        String name = rateLimiter.name();
        if (StringUtils.isBlank(name)) {
            name = nameGenerator.generate(target, method, args);
        }
        // 解析 Key
        String key = rateLimiter.key();
        if (StringUtils.isNotBlank(key)) {
            Object eval = ExpressionUtils.eval(key, target, method, args);
            if (eval == null) {
                throw RateLimiterErrorCode.RATE_LIMITER_KEY_EVAL_EXCEPTION.toException();
            }
            key = String.valueOf(eval);
        }
        // 获取后缀
        String suffix = switch (rateLimiter.type()) {
            case IP -> getRequestIp();
            case CLUSTER -> redissonClient.getId();
            default -> StringUtils.EMPTY;
        };

        return CacheUtil.buildKey(configuration.getKeyPrefix(), name, key, suffix);
    }

    private String getRequestIp() {
        HttpServletRequest request = Optional
                .ofNullable(RequestContextHolder.getRequestAttributes())
                .map(it -> (ServletRequestAttributes) it)
                .map(ServletRequestAttributes::getRequest)
                .orElseThrow(RateLimiterErrorCode.RATE_LIMITER_GET_IP_EXCEPTION::toException);

        for (String header : List.of(
                "X-Forwarded-For",
                "X-Real-IP",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                "HTTP_CLIENT_IP",
                "HTTP_X_FORWARDED_FOR")) {
            String ip = request.getHeader(header);
            if (StringUtils.isNotEmpty(ip) && !"unknown".equalsIgnoreCase(ip)) {
                if ("X-Forwarded-For".equalsIgnoreCase(header)) {
                    ip = StringUtils.split(ip, ',')[0].trim();
                }
                return NetworkUtil.getMultistageReverseProxyIp(ip);
            }
        }
        return NetworkUtil.getMultistageReverseProxyIp(request.getRemoteAddr());
    }

    /**
     * 判断是否需要更新限流器配置
     *
     * @param rRateLimiter 限流器
     * @param rateType     限流类型（OVERALL：全局限流；PER_CLIENT：单机限流）
     * @param rate         速率（指定时间间隔产生的令牌数）
     * @param rateInterval 速率间隔
     * @return 是否需要更新配置
     */
    private boolean shouldUpdate(RRateLimiter rRateLimiter,
                                 RateType rateType,
                                 long rate,
                                 Duration rateInterval) {
        RateLimiterConfig config = rRateLimiter.getConfig();
        return !Objects.equals(config.getRateType(), rateType) ||
                !Objects.equals(config.getRate(), rate) ||
                !Objects.equals(config.getRateInterval(), rateInterval.toMillis());
    }
}
