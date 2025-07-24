package com.epam.core.exception;

import com.epam.core.exception.core.BaseRuntimeException;
import org.springframework.http.HttpStatus;

import java.util.Map;

public class SongAlreadyExistException extends BaseRuntimeException {

    private static final int errorHttpStatusCode = HttpStatus.CONFLICT.value();
    private static final String errorHttpMessage = "Metadata extract error.";

    public SongAlreadyExistException(String errorMessage) {
        super(errorMessage, errorHttpStatusCode);
    }

    public SongAlreadyExistException(String errorMessage, HttpStatus httpStatus) {
        super(errorMessage, httpStatus);
    }

    public SongAlreadyExistException(String errorMessage, int httpStatus) {
        super(errorMessage, httpStatus);
    }

    public SongAlreadyExistException(Map<String, String> errorDetails) {
        super(errorHttpMessage, errorHttpStatusCode, errorDetails);
    }

    public SongAlreadyExistException(HttpStatus httpStatus, Map<String, String> errorDetails) {
        super(errorHttpMessage, httpStatus, errorDetails);
    }

    public SongAlreadyExistException(String errorMessage, Map<String, String> errorDetails) {
        super(errorMessage, errorHttpStatusCode, errorDetails);
    }

    public SongAlreadyExistException(String errorMessage, HttpStatus httpStatus, Map<String, String> errorDetails) {
        super(errorMessage, httpStatus, errorDetails);
    }

    public SongAlreadyExistException(String errorMessage, int httpStatus, Map<String, String> errorDetails) {
        super(errorMessage, httpStatus, errorDetails);
    }
}
