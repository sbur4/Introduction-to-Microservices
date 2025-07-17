package com.epam.core.exception;

import com.epam.core.exception.core.BaseRuntimeException;
import org.springframework.http.HttpStatus;

import java.util.Map;

public class DeleteMetadataByIdsException extends BaseRuntimeException {

    private static final int errorHttpStatusCode = HttpStatus.BAD_REQUEST.value();
    private static final String errorHttpMessage = "Delete metadata by ID's error.";

    public DeleteMetadataByIdsException(Map<String, String> errorDetails) {
        super(errorHttpMessage, errorHttpStatusCode, errorDetails);
    }

    public DeleteMetadataByIdsException(HttpStatus httpStatus, Map<String, String> errorDetails) {
        super(errorHttpMessage, httpStatus, errorDetails);
    }

    public DeleteMetadataByIdsException(String errorMessage, Map<String, String> errorDetails) {
        super(errorMessage, errorHttpStatusCode, errorDetails);
    }
}