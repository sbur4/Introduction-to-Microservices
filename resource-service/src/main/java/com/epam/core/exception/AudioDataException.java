package com.epam.core.exception;

import com.epam.web.model.ApiErrorResponse;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AudioDataException extends RuntimeException {
    private final ApiErrorResponse apiErrorResponse;

    public AudioDataException(String errorMessage) {
        int errorHttpCode = HttpStatus.BAD_REQUEST.value();
        this.apiErrorResponse = ApiErrorResponse.builder()
                .errorMessage(errorMessage)
                .errorCode(errorHttpCode)
                .build();
    }
}
