package com.epam.core.exception;

import com.epam.core.exception.core.BaseRuntimeException;
import org.springframework.http.HttpStatus;

import java.util.Map;

public class MetadataAlreadyExistException extends BaseRuntimeException {

    private static final int errorHttpStatusCode = HttpStatus.CONFLICT.value();
    private static final String errorHttpMessage = "Metadata already exist error.";

    public MetadataAlreadyExistException(String errorMessage, Map<String, String> errorDetails) {
        super(errorMessage, errorHttpStatusCode, errorDetails);
    }

    public MetadataAlreadyExistException(String errorMessage) {
        super(errorMessage, errorHttpStatusCode);
    }
}
