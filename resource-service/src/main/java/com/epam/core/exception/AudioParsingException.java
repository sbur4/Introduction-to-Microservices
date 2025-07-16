package com.epam.core.exception;

import com.epam.web.model.ApiErrorResponse;
import com.epam.web.model.ApiErrorResponseDetails;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Getter
public class AudioParsingException extends RuntimeException {
    private final ApiErrorResponse apiErrorResponse;

    public AudioParsingException(String errorMessage, Map<String, String> errorDetails) {
        int errorHttpCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
        this.apiErrorResponse = ApiErrorResponseDetails.builder()
                .errorMessage(errorMessage)
                .errorDetails(errorDetails)
                .errorCode(errorHttpCode)
                .build();
    }
}
