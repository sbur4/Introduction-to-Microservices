package com.epam.core.exception;

import com.epam.core.exception.core.BaseRuntimeException;
import org.springframework.http.HttpStatus;

import java.util.Map;

public class MetadataExtractException extends BaseRuntimeException {

    private static final int errorHttpStatusCode = HttpStatus.BAD_REQUEST.value();
    private static final String errorHttpMessage = "Metadata extract error.";

    public MetadataExtractException(String errorMessage) {
        super(errorMessage, errorHttpStatusCode);
    }

    public MetadataExtractException(String errorMessage, HttpStatus httpStatus) {
        super(errorMessage, httpStatus);
    }

    public MetadataExtractException(String errorMessage, int httpStatus) {
        super(errorMessage, httpStatus);
    }

    public MetadataExtractException(Map<String, String> errorDetails) {
        super(errorHttpMessage, errorHttpStatusCode, errorDetails);
    }

    public MetadataExtractException(HttpStatus httpStatus, Map<String, String> errorDetails) {
        super(errorHttpMessage, httpStatus, errorDetails);
    }

    public MetadataExtractException(String errorMessage, Map<String, String> errorDetails) {
        super(errorMessage, errorHttpStatusCode, errorDetails);
    }

    public MetadataExtractException(String errorMessage, HttpStatus httpStatus, Map<String, String> errorDetails) {
        super(errorMessage, httpStatus, errorDetails);
    }

    public MetadataExtractException(String errorMessage, int httpStatus, Map<String, String> errorDetails) {
        super(errorMessage, httpStatus, errorDetails);
    }
}
