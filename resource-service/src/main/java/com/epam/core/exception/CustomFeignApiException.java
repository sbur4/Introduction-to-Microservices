package com.epam.core.exception;

import com.epam.core.exception.core.BaseRuntimeException;
import com.epam.core.exception.model.ApiErrorModel;
import org.springframework.http.HttpStatus;

public class CustomFeignApiException extends BaseRuntimeException {

    private final HttpStatus errorHttpStatusCode;

    public CustomFeignApiException(ApiErrorModel apiErrorModel, int status) {
        super(apiErrorModel);
        this.errorHttpStatusCode = HttpStatus.valueOf(status);
    }
}
