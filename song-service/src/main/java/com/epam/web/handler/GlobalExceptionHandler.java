package com.epam.web.handler;

import com.epam.core.exception.DeleteMetadataByIdsException;
import com.epam.core.exception.GetMetadataByIdException;
import com.epam.core.exception.MetadataAlreadyExistException;
import com.epam.core.exception.model.ApiErrorModel;
import com.epam.core.exception.model.ApiErrorModelDetails;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
@FieldDefaults(makeFinal = true, level = AccessLevel.PACKAGE)
public class GlobalExceptionHandler {

    static String VALIDATION_FAILED = "Validation failed";
    static String INTERNAL_ERROR_MSG = "An unexpected error occurred";

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorModel> handleGenericException(Exception ex) {
        log.warn("{}: '{}'", INTERNAL_ERROR_MSG, ex.getMessage());
        ApiErrorModel response = ApiErrorModel.builder()
                .errorMessage(ex.getMessage())
                .errorCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .build();
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiErrorModel> handleNoResourceFoundException(NoResourceFoundException ex) {
        log.warn("{}: '{}'", INTERNAL_ERROR_MSG, ex.getMessage());
        ApiErrorModel response = ApiErrorModel.builder()
                .errorMessage(ex.getMessage())
                .errorCode(HttpStatus.SERVICE_UNAVAILABLE.value())
                .build();
        return new ResponseEntity<>(response, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorModel> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        log.warn("{}: '{}'", INTERNAL_ERROR_MSG, ex.getMessage());
        ApiErrorModel response = ApiErrorModel.builder()
                .errorMessage(ex.getMessage())
                .errorCode(HttpStatus.BAD_REQUEST.value())
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiErrorModel> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException ex) {
        log.warn("{}: '{}'", INTERNAL_ERROR_MSG, ex.getMessage());
        ApiErrorModel response = ApiErrorModel.builder()
                .errorMessage(ex.getMessage())
                .errorCode(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value())
                .build();
        return new ResponseEntity<>(response, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @ExceptionHandler(DeleteMetadataByIdsException.class)
    public ResponseEntity<ApiErrorModel> handleDeleteMetadataByIdsException(DeleteMetadataByIdsException ex) {
        log.warn("{} {}: '{}'", INTERNAL_ERROR_MSG, " when delete metadata by ID's", ex.getApiErrorModel().getErrorMessage());
        return new ResponseEntity<>(ex.getApiErrorModel(), ex.getHttpStatus());
    }

    @ExceptionHandler(GetMetadataByIdException.class)
    public ResponseEntity<ApiErrorModel> handleGetMetadataByIdException(GetMetadataByIdException ex) {
        log.warn("{} {}: '{}'", INTERNAL_ERROR_MSG, " when get metadata by ID", ex.getApiErrorModel().getErrorMessage());
        return new ResponseEntity<>(ex.getApiErrorModel(), ex.getHttpStatus());
    }

    @ExceptionHandler(MetadataAlreadyExistException.class)
    public ResponseEntity<ApiErrorModel> handleMetadataAlreadyExistException(MetadataAlreadyExistException ex) {
        log.warn("{} {}: '{}'", INTERNAL_ERROR_MSG, " when metadata already exist", ex.getApiErrorModel().getErrorMessage());
        return new ResponseEntity<>(ex.getApiErrorModel(), ex.getHttpStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorModel> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.warn("{}: '{}'", VALIDATION_FAILED, ex.getMessage());

        Map<String, String> errorDetails = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        fieldError -> "Field: '{%s}' with value: '{%s}'".formatted(fieldError.getField(), fieldError.getRejectedValue()),
                        fieldError -> Optional.ofNullable(fieldError.getDefaultMessage()).orElse("Invalid value."),
                        (existing, replacement) -> existing
                ));

        ApiErrorModelDetails response = ApiErrorModelDetails.builder()
                .errorMessage(VALIDATION_FAILED)
                .errorCode(HttpStatus.BAD_REQUEST.value())
                .errorDetails(errorDetails)
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ApiErrorModel> handleHandlerMethodValidationException(HandlerMethodValidationException ex) {
        log.warn("{}: '{}'", VALIDATION_FAILED, ex.getMessage());

        Map<String, String> errorDetails = ex.getParameterValidationResults().stream()
                .flatMap(paramResult -> {
                    Object argument = paramResult.getArgument();
                    String parameterName = paramResult.getMethodParameter().getParameterName();
                    String argString = (argument != null) ? argument.toString() : "null";

                    return paramResult.getResolvableErrors().stream()
                            .map(resolvableError -> Map.entry(
                                    "Parameter '{%s}' with value '{%s}'".formatted(parameterName, argString),
                                    resolvableError.getDefaultMessage()
                            ));
                })
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (existingValue, newValue) -> existingValue + " | " + newValue
                ));

        ApiErrorModelDetails responseDetails = ApiErrorModelDetails.builder()
                .errorMessage(VALIDATION_FAILED)
                .errorCode(HttpStatus.BAD_REQUEST.value())
                .errorDetails(errorDetails)
                .build();
        return new ResponseEntity<>(responseDetails, HttpStatus.BAD_REQUEST);
    }
}
