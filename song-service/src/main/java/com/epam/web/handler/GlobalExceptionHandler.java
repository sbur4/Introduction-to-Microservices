package com.epam.web.handler;

import com.epam.core.exception.DeleteMetadataByIdsException;
import com.epam.core.exception.GetMetadataByIdException;
import com.epam.core.exception.MetadataAlreadyExistException;
import com.epam.web.model.ApiErrorResponse;
import com.epam.web.model.ApiErrorResponseDetails;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
@FieldDefaults(makeFinal = true, level = AccessLevel.PACKAGE)
public class GlobalExceptionHandler {

    static String VALIDATION_EX = "Validation failed";

    @ExceptionHandler({Exception.class, NoResourceFoundException.class, HttpMessageNotReadableException.class,
            HttpMediaTypeNotSupportedException.class})
    public ResponseEntity<ApiErrorResponse> handleGenericException(Exception ex) {
        log.warn("Exception encountered: '{}'", ex.getMessage());
        ApiErrorResponse responseDetails = ApiErrorResponse.builder()
                .errorMessage(ex.getMessage())
                .errorCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .build();
        return new ResponseEntity<>(responseDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.warn(VALIDATION_EX + ": '{}'", ex.getMessage());

        String errorMessage = VALIDATION_EX;
        Map<String, String> errorDetails = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        fieldError -> "Field: '{%s}' with value: '{%s}'".formatted(fieldError.getField(), fieldError.getRejectedValue()),
                        FieldError::getDefaultMessage,
                        (existing, replacement) -> existing
                ));

        ApiErrorResponseDetails responseDetails = ApiErrorResponseDetails.builder()
                .errorMessage(errorMessage)
                .errorCode(HttpStatus.BAD_REQUEST.value())
                .errorDetails(errorDetails)
                .build();
        return new ResponseEntity<>(responseDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DeleteMetadataByIdsException.class)
    public ResponseEntity<ApiErrorResponse> handleDeleteMetadataByIdsException(DeleteMetadataByIdsException ex) {
        log.warn("DeleteMetadataByIdsException occurred: '{}'", ex.getApiErrorResponse().getErrorMessage());
        return new ResponseEntity<>(ex.getApiErrorResponse(), HttpStatus.resolve(ex.getApiErrorResponse().getErrorCode()));
    }

    @ExceptionHandler(GetMetadataByIdException.class)
    public ResponseEntity<ApiErrorResponse> handleGetMetadataByIdException(GetMetadataByIdException ex) {
        log.warn("GetMetadataByIdException occurred: '{}'", ex.getApiErrorResponse().getErrorMessage());
        return new ResponseEntity<>(ex.getApiErrorResponse(), HttpStatus.resolve(ex.getApiErrorResponse().getErrorCode()));
    }

    @ExceptionHandler(MetadataAlreadyExistException.class)
    public ResponseEntity<ApiErrorResponse> handleMetadataAlreadyExistException(MetadataAlreadyExistException ex) {
        log.warn("MetadataAlreadyExistException occurred: '{}'", ex.getApiErrorResponse().getErrorMessage());
        return new ResponseEntity<>(ex.getApiErrorResponse(), HttpStatus.resolve(ex.getApiErrorResponse().getErrorCode()));
    }
}
