package com.epam.core.exception;

import com.epam.web.model.ApiErrorResponse;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class DurationFormatException extends RuntimeException {
    private final ApiErrorResponse apiErrorResponse;

    public DurationFormatException(String errorMessage) {
        int errorHttpCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
        this.apiErrorResponse = ApiErrorResponse.builder()
                .errorMessage(errorMessage)
                .errorCode(errorHttpCode)
                .build();
    }
}
