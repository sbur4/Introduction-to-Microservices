package com.epam.core.exception;

import com.epam.core.exception.core.BaseRuntimeException;
import org.springframework.http.HttpStatus;

import java.util.Map;

public class DurationFormatException extends BaseRuntimeException {

    private static final int errorHttpStatusCode = HttpStatus.BAD_REQUEST.value();
    private static final String errorHttpMessage = "Duration format error.";

    public DurationFormatException(String errorMessage) {
        super(errorMessage, errorHttpStatusCode);
    }

    public DurationFormatException(String errorMessage, HttpStatus httpStatus) {
        super(errorMessage, httpStatus);
    }

    public DurationFormatException(String errorMessage, int httpStatus) {
        super(errorMessage, httpStatus);
    }

    public DurationFormatException(Map<String, String> errorDetails) {
        super(errorHttpMessage, errorHttpStatusCode, errorDetails);
    }

    public DurationFormatException(HttpStatus httpStatus, Map<String, String> errorDetails) {
        super(errorHttpMessage, httpStatus, errorDetails);
    }

    public DurationFormatException(String errorMessage, Map<String, String> errorDetails) {
        super(errorMessage, errorHttpStatusCode, errorDetails);
    }

    public DurationFormatException(String errorMessage, HttpStatus httpStatus, Map<String, String> errorDetails) {
        super(errorMessage, httpStatus, errorDetails);
    }

    public DurationFormatException(String errorMessage, int httpStatus, Map<String, String> errorDetails) {
        super(errorMessage, httpStatus, errorDetails);
    }
}
