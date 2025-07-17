package com.epam.core.exception;

import com.epam.core.exception.core.BaseRuntimeException;
import org.springframework.http.HttpStatus;

import java.util.Map;

public class GetSongByIdException extends BaseRuntimeException {

    private static final int errorHttpStatusCode = HttpStatus.BAD_REQUEST.value();
    private static final String errorHttpMessage = "Duration format error.";

    public GetSongByIdException(String errorMessage) {
        super(errorMessage, errorHttpStatusCode);
    }

    public GetSongByIdException(String errorMessage, HttpStatus httpStatus) {
        super(errorMessage, httpStatus);
    }

    public GetSongByIdException(String errorMessage, int httpStatus) {
        super(errorMessage, httpStatus);
    }

    public GetSongByIdException(Map<String, String> errorDetails) {
        super(errorHttpMessage, errorHttpStatusCode, errorDetails);
    }

    public GetSongByIdException(HttpStatus httpStatus, Map<String, String> errorDetails) {
        super(errorHttpMessage, httpStatus, errorDetails);
    }

    public GetSongByIdException(String errorMessage, Map<String, String> errorDetails) {
        super(errorMessage, errorHttpStatusCode, errorDetails);
    }

    public GetSongByIdException(String errorMessage, HttpStatus httpStatus, Map<String, String> errorDetails) {
        super(errorMessage, httpStatus, errorDetails);
    }

    public GetSongByIdException(String errorMessage, int httpStatus, Map<String, String> errorDetails) {
        super(errorMessage, httpStatus, errorDetails);
    }
}
