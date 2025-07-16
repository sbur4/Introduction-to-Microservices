package com.epam.core.exception;

import com.epam.web.model.ApiErrorResponse;
import com.epam.web.model.ApiErrorResponseDetails;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Map;

public class DeleteMetadataByIdsException extends RuntimeException {

    @Getter
    private final ApiErrorResponse apiErrorResponse;

    private final String errorHttpMessage = "Validation failure";
    private int errorHttpCode = HttpStatus.BAD_REQUEST.value();

    public DeleteMetadataByIdsException(Map<String, String> errorDetails) {
        this.apiErrorResponse = ApiErrorResponseDetails.builder()
                .errorMessage(errorHttpMessage)
                .errorCode(errorHttpCode)
                .errorDetails(errorDetails)
                .build();
    }

    public DeleteMetadataByIdsException(HttpStatus httpStatus, Map<String, String> errorDetails) {
        this.errorHttpCode = httpStatus.value();
        this.apiErrorResponse = ApiErrorResponseDetails.builder()
                .errorMessage(errorHttpMessage)
                .errorCode(errorHttpCode)
                .errorDetails(errorDetails)
                .build();
    }
}
