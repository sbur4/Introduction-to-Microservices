package com.epam.core.exception;

import com.epam.core.exception.core.BaseRuntimeException;
import org.springframework.http.HttpStatus;

import java.util.Map;

public class GetMetadataByIdException extends BaseRuntimeException {

    private static final int errorHttpStatusCode = HttpStatus.BAD_REQUEST.value();
    private static final String errorHttpMessage = "Get metadata by ID error.";

    public GetMetadataByIdException(String errorMessage, HttpStatus httpStatus) {
        super(errorMessage, httpStatus);
    }

    public GetMetadataByIdException(Map<String, String> errorDetails) {
        super(errorHttpMessage, errorHttpStatusCode, errorDetails);
    }

    public GetMetadataByIdException(String errorMessage) {
        super(errorMessage, errorHttpStatusCode);
    }
}
