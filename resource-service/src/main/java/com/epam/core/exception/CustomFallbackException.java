package com.epam.core.exception;

import com.epam.core.exception.core.BaseRuntimeException;
import org.springframework.http.HttpStatus;

public class CustomFallbackException extends BaseRuntimeException {

    private static final int errorHttpStatusCode = HttpStatus.SERVICE_UNAVAILABLE.value();

    public CustomFallbackException(String errorMessage) {
        super(errorMessage, errorHttpStatusCode);
    }
}
