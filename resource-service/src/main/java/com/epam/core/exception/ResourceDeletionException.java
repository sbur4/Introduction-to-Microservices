package com.epam.core.exception;

import com.epam.core.exception.core.BaseRuntimeException;
import org.springframework.http.HttpStatus;

import java.util.Map;

public class ResourceDeletionException extends BaseRuntimeException {

    private static final int errorHttpStatusCode = HttpStatus.BAD_REQUEST.value();
    private static final String errorHttpMessage = "Metadata extract error.";

    public ResourceDeletionException(String errorMessage) {
        super(errorMessage, errorHttpStatusCode);
    }

    public ResourceDeletionException(String errorMessage, HttpStatus httpStatus) {
        super(errorMessage, httpStatus);
    }

    public ResourceDeletionException(String errorMessage, int httpStatus) {
        super(errorMessage, httpStatus);
    }

    public ResourceDeletionException(Map<String, String> errorDetails) {
        super(errorHttpMessage, errorHttpStatusCode, errorDetails);
    }

    public ResourceDeletionException(HttpStatus httpStatus, Map<String, String> errorDetails) {
        super(errorHttpMessage, httpStatus, errorDetails);
    }

    public ResourceDeletionException(String errorMessage, Map<String, String> errorDetails) {
        super(errorMessage, errorHttpStatusCode, errorDetails);
    }

    public ResourceDeletionException(String errorMessage, HttpStatus httpStatus, Map<String, String> errorDetails) {
        super(errorMessage, httpStatus, errorDetails);
    }

    public ResourceDeletionException(String errorMessage, int httpStatus, Map<String, String> errorDetails) {
        super(errorMessage, httpStatus, errorDetails);
    }
}
