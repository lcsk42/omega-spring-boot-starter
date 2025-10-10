package com.lcsk42.frameworks.starter.ratelimiter.exception;

import com.lcsk42.frameworks.starter.convention.exception.ServiceException;

public class RateLimiterException extends ServiceException {
    public RateLimiterException(String message) {
        super(message);
    }
}
