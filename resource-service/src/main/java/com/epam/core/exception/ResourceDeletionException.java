package com.epam.core.exception;

import com.epam.web.model.ApiErrorResponse;
import com.epam.web.model.ApiErrorResponseDetails;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Getter
public class ResourceDeletionException extends RuntimeException{
    private final ApiErrorResponse apiErrorResponse;

    public ResourceDeletionException(String errorMessage, Map<String, String> errorDetails) {
        int errorHttpCode = HttpStatus.PERMANENT_REDIRECT.value();
        this.apiErrorResponse = ApiErrorResponseDetails.builder()
                .errorMessage(errorMessage)
                .errorCode(errorHttpCode)
                .errorDetails(errorDetails)
                .build();
    }
}
