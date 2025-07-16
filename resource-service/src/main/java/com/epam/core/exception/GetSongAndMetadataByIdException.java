package com.epam.core.exception;

import com.epam.web.model.ApiErrorResponse;
import com.epam.web.model.ApiErrorResponseDetails;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Getter
public class GetSongAndMetadataByIdException extends RuntimeException {

    private final ApiErrorResponse apiErrorResponse;

    public GetSongAndMetadataByIdException(String errorMessage, HttpStatus httpStatus) {
        this.apiErrorResponse = ApiErrorResponse.builder()
                .errorMessage(errorMessage)
                .errorCode(httpStatus.value())
                .build();
    }

    public GetSongAndMetadataByIdException(Map<String, String> errorDetails) {
        String errorHttpMessage = "Validation failure";
        this.apiErrorResponse = ApiErrorResponseDetails.builder()
                .errorMessage(errorHttpMessage)
                .errorCode(HttpStatus.BAD_REQUEST.value())
                .errorDetails(errorDetails)
                .build();
    }
}
