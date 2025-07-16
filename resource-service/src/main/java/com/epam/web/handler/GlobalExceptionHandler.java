package com.epam.web.handler;

import com.epam.core.exception.AudioDataException;
import com.epam.core.exception.AudioParsingException;
import com.epam.core.exception.DeleteSongAndMetadataByIdsException;
import com.epam.core.exception.DurationFormatException;
import com.epam.core.exception.GetSongAndMetadataByIdException;
import com.epam.core.exception.ResourceDeletionException;
import com.epam.core.exception.SongAlreadyExistException;
import com.epam.web.model.ApiErrorResponse;
import com.epam.web.model.ApiErrorResponseDetails;
import feign.FeignException;
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

    @ExceptionHandler({Exception.class, NoResourceFoundException.class, HttpMessageNotReadableException.class,
            HttpMediaTypeNotSupportedException.class, IllegalArgumentException.class})
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
        log.warn("Validation failed" + ": '{}'", ex.getMessage());

        String errorMessage = "Validation failed";
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

    @ExceptionHandler(AudioDataException.class)
    public ResponseEntity<ApiErrorResponse> handleAudioDataException(AudioDataException ex) {
        log.warn("AudioDataException occurred: '{}'", ex.getApiErrorResponse().getErrorMessage());
        return new ResponseEntity<>(ex.getApiErrorResponse(), HttpStatus.resolve(ex.getApiErrorResponse().getErrorCode()));
    }

    @ExceptionHandler(AudioParsingException.class)
    public ResponseEntity<ApiErrorResponse> handleAudioParsingException(AudioParsingException ex) {
        log.warn("AudioParsingException occurred: '{}'", ex.getApiErrorResponse().getErrorMessage());
        return new ResponseEntity<>(ex.getApiErrorResponse(), HttpStatus.resolve(ex.getApiErrorResponse().getErrorCode()));
    }

    @ExceptionHandler(DeleteSongAndMetadataByIdsException.class)
    public ResponseEntity<ApiErrorResponse> handleDeleteSongAndMetadataByIdsException(DeleteSongAndMetadataByIdsException ex) {
        log.warn("DeleteSongAndMetadataByIdsException occurred: '{}'", ex.getApiErrorResponse().getErrorMessage());
        return new ResponseEntity<>(ex.getApiErrorResponse(), HttpStatus.resolve(ex.getApiErrorResponse().getErrorCode()));
    }

    @ExceptionHandler(DurationFormatException.class)
    public ResponseEntity<ApiErrorResponse> handleDurationFormatException(DurationFormatException ex) {
        log.warn("DurationFormatException occurred: '{}'", ex.getApiErrorResponse().getErrorMessage());
        return new ResponseEntity<>(ex.getApiErrorResponse(), HttpStatus.resolve(ex.getApiErrorResponse().getErrorCode()));
    }

    @ExceptionHandler(GetSongAndMetadataByIdException.class)
    public ResponseEntity<ApiErrorResponse> handleGetSongAndMetadataByIdException(GetSongAndMetadataByIdException ex) {
        log.warn("GetSongAndMetadataByIdException occurred: '{}'", ex.getApiErrorResponse().getErrorMessage());
        return new ResponseEntity<>(ex.getApiErrorResponse(), HttpStatus.resolve(ex.getApiErrorResponse().getErrorCode()));
    }

    @ExceptionHandler(ResourceDeletionException.class)
    public ResponseEntity<ApiErrorResponse> handleResourceDeletionException(ResourceDeletionException ex) {
        log.warn("ResourceDeletionException occurred: '{}'", ex.getApiErrorResponse().getErrorMessage());
        return new ResponseEntity<>(ex.getApiErrorResponse(), HttpStatus.resolve(ex.getApiErrorResponse().getErrorCode()));
    }

    @ExceptionHandler(SongAlreadyExistException.class)
    public ResponseEntity<ApiErrorResponse> handleSongAlreadyExistException(SongAlreadyExistException ex) {
        log.warn("SongAlreadyExistException occurred: '{}'", ex.getApiErrorResponse().getErrorMessage());
        return new ResponseEntity<>(ex.getApiErrorResponse(), HttpStatus.resolve(ex.getApiErrorResponse().getErrorCode()));
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ApiErrorResponse> handleFeignClientException(FeignException ex) {
        log.warn("FeignException occurred: '{}'", ex.getMessage());
        ApiErrorResponse responseDetails = ApiErrorResponse.builder()
                .errorMessage(ex.getMessage())
                .errorCode(HttpStatus.BAD_REQUEST.value())
                .build();
        return new ResponseEntity<>(responseDetails, HttpStatus.resolve(ex.status()));
    }
}
