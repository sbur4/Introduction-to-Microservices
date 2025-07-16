package com.epam.core.exception;

import com.epam.web.model.ApiErrorResponse;
import com.epam.web.model.ApiErrorResponseDetails;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Map;

public class GetMetadataByIdException extends RuntimeException {

    @Getter
    private final ApiErrorResponse apiErrorResponse;

    private final String errorHttpMessage = "Validation failure";

    public GetMetadataByIdException(String errorMessage, HttpStatus httpStatus) {
        this.apiErrorResponse = ApiErrorResponse.builder()
                .errorMessage(errorMessage)
                .errorCode(httpStatus.value())
                .build();
    }

    public GetMetadataByIdException(Map<String, String> errorDetails) {
        this.apiErrorResponse = ApiErrorResponseDetails.builder()
                .errorMessage(errorHttpMessage)
                .errorCode(HttpStatus.BAD_REQUEST.value())
                .errorDetails(errorDetails)
                .build();
    }
}
