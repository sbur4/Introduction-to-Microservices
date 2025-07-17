package com.epam.core.exception;

import com.epam.core.exception.core.BaseRuntimeException;
import com.epam.core.exception.model.ApiErrorResponse;
import com.epam.core.exception.model.ApiErrorResponseDetails;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Map;

public class DeleteSongAndMetadataByIdsException extends BaseRuntimeException {

    private static final int errorHttpStatusCode = HttpStatus.BAD_REQUEST.value();
    private static final String errorHttpMessage = "Delete song and metadata by ID's error.";

    public DeleteSongAndMetadataByIdsException(String errorMessage) {
        super(errorMessage, errorHttpStatusCode);
    }

    public DeleteSongAndMetadataByIdsException(String errorMessage, HttpStatus httpStatus) {
        super(errorMessage, httpStatus);
    }

    public DeleteSongAndMetadataByIdsException(String errorMessage, int httpStatus) {
        super(errorMessage, httpStatus);
    }

    public DeleteSongAndMetadataByIdsException(Map<String, String> errorDetails) {
        super(errorHttpMessage, errorHttpStatusCode, errorDetails);
    }

    public DeleteSongAndMetadataByIdsException(HttpStatus httpStatus, Map<String, String> errorDetails) {
        super(errorHttpMessage, httpStatus, errorDetails);
    }

    public DeleteSongAndMetadataByIdsException(String errorMessage, Map<String, String> errorDetails) {
        super(errorMessage, errorHttpStatusCode, errorDetails);
    }

    public DeleteSongAndMetadataByIdsException(String errorMessage, HttpStatus httpStatus, Map<String, String> errorDetails) {
        super(errorMessage, httpStatus, errorDetails);
    }

    public DeleteSongAndMetadataByIdsException(String errorMessage, int httpStatus, Map<String, String> errorDetails) {
        super(errorMessage, httpStatus, errorDetails);
    }
}
