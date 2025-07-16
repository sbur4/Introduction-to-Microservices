package com.epam.core.exception;

import com.epam.web.model.ApiErrorResponse;
import com.epam.web.model.ApiErrorResponseDetails;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Map;

public class MetadataAlreadyExistException extends RuntimeException {

    @Getter
    private final ApiErrorResponse apiErrorResponse;

    private final int errorHttpCode = HttpStatus.CONFLICT.value();

    public MetadataAlreadyExistException(String errorMessage, Map<String, String> errorDetails) {
        this.apiErrorResponse = ApiErrorResponseDetails.builder()
                .errorMessage(errorMessage)
                .errorCode(errorHttpCode)
                .errorDetails(errorDetails)
                .build();
    }

    public MetadataAlreadyExistException(String errorMessage) {
        this.apiErrorResponse = ApiErrorResponseDetails.builder()
                .errorMessage(errorMessage)
                .errorCode(errorHttpCode)
                .build();
    }
}
