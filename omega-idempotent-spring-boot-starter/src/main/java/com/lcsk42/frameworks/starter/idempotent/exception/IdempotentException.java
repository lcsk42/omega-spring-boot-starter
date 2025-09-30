package com.lcsk42.frameworks.starter.idempotent.exception;

import com.lcsk42.frameworks.starter.convention.exception.ServiceException;

public class IdempotentException extends ServiceException {
    public IdempotentException(String message) {
        super(message);
    }
}
