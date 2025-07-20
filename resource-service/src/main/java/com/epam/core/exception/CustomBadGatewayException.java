package com.epam.core.exception;

import com.epam.core.exception.core.BaseRuntimeException;
import org.springframework.http.HttpStatus;

public class CustomBadGatewayException extends BaseRuntimeException {

    private static final int errorHttpStatusCode = HttpStatus.BAD_GATEWAY.value();

    public CustomBadGatewayException(String errorMessage) {
        super(errorMessage, errorHttpStatusCode);
    }
}
