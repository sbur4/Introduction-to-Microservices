package com.epam.web.handler;

import com.epam.core.exception.AudioDataException;
import com.epam.core.exception.AudioParsingException;
import com.epam.core.exception.CustomBadGatewayException;
import com.epam.core.exception.CustomFallbackException;
import com.epam.core.exception.CustomFeignApiException;
import com.epam.core.exception.DeleteSongAndMetadataByIdsException;
import com.epam.core.exception.DurationFormatException;
import com.epam.core.exception.GetSongByIdException;
import com.epam.core.exception.ResourceDeletionException;
import com.epam.core.exception.MetadataExtractException;
import com.epam.core.exception.model.ApiErrorModel;
import com.epam.core.exception.model.ApiErrorModelDetails;
import feign.FeignException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Map;
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

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorModel> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("{}: '{}'", INTERNAL_ERROR_MSG, ex.getMessage());
        ApiErrorModel response = ApiErrorModel.builder()
                .errorMessage(ex.getMessage())
                .errorCode(HttpStatus.BAD_REQUEST.value())
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorModel> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        log.warn("{}: '{}'", INTERNAL_ERROR_MSG, ex.getMessage());
        ApiErrorModel response = ApiErrorModel.builder()
                .errorMessage("%s for input string: '%s', with value: '%s'".formatted(VALIDATION_FAILED, ex.getName(), ex.getValue()))
                .errorCode(HttpStatus.BAD_REQUEST.value())
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
                                    "Parameter '{%s}' with value '{%s}' ".formatted(parameterName, argString),
                                    resolvableError.getDefaultMessage()
                            ));
                })
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (existingValue, newValue) -> existingValue + " | " + newValue
                ));

        ApiErrorModel response = ApiErrorModel.builder()
                .errorMessage("%s: %s".formatted(VALIDATION_FAILED, errorDetails))
                .errorCode(HttpStatus.BAD_REQUEST.value())
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorModel> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.warn("{}: '{}'", VALIDATION_FAILED, ex.getMessage());

        Map<String, String> errorDetails = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage,
                        (existing, replacement) -> existing
                ));

        ApiErrorModelDetails response = ApiErrorModelDetails.builder()
                .errorMessage(VALIDATION_FAILED)
                .errorCode(HttpStatus.BAD_REQUEST.value())
                .errorDetails(errorDetails)
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AudioDataException.class)
    public ResponseEntity<ApiErrorModel> handleAudioDataException(AudioDataException ex) {
        log.warn("{} {}: '{}'", INTERNAL_ERROR_MSG, " when validate audio data", ex.getApiErrorModel().getErrorMessage());
        return new ResponseEntity<>(ex.getApiErrorModel(), ex.getHttpStatus());
    }

    @ExceptionHandler(AudioParsingException.class)
    public ResponseEntity<ApiErrorModel> handleAudioParsingException(AudioParsingException ex) {
        log.warn("{} {}: '{}'", INTERNAL_ERROR_MSG, " when parse audio data", ex.getApiErrorModel().getErrorMessage());
        return new ResponseEntity<>(ex.getApiErrorModel(), ex.getHttpStatus());
    }

    @ExceptionHandler(DeleteSongAndMetadataByIdsException.class)
    public ResponseEntity<ApiErrorModel> handleDeleteSongAndMetadataByIdsException(DeleteSongAndMetadataByIdsException ex) {
        log.warn("{} {}: '{}'", INTERNAL_ERROR_MSG, " when delete song and metadata by ID's", ex.getApiErrorModel().getErrorMessage());
        return new ResponseEntity<>(ex.getApiErrorModel(), ex.getHttpStatus());
    }

    @ExceptionHandler(DurationFormatException.class)
    public ResponseEntity<ApiErrorModel> handleDurationFormatException(DurationFormatException ex) {
        log.warn("{} {}: '{}'", INTERNAL_ERROR_MSG, " when validate duration", ex.getApiErrorModel().getErrorMessage());
        return new ResponseEntity<>(ex.getApiErrorModel(), ex.getHttpStatus());
    }

    @ExceptionHandler(GetSongByIdException.class)
    public ResponseEntity<ApiErrorModel> handleGetSongByIdException(GetSongByIdException ex) {
        log.warn("{} {}: '{}'", INTERNAL_ERROR_MSG, " when get song by ID", ex.getApiErrorModel().getErrorMessage());
        return new ResponseEntity<>(ex.getApiErrorModel(), ex.getHttpStatus());
    }

    @ExceptionHandler(ResourceDeletionException.class)
    public ResponseEntity<ApiErrorModel> handleResourceDeletionException(ResourceDeletionException ex) {
        log.warn("{} {}: '{}'", INTERNAL_ERROR_MSG, " when resource delete", ex.getApiErrorModel().getErrorMessage());
        return new ResponseEntity<>(ex.getApiErrorModel(), ex.getHttpStatus());
    }

    @ExceptionHandler(MetadataExtractException.class)
    public ResponseEntity<ApiErrorModel> handleSongAlreadyExistException(MetadataExtractException ex) {
        log.warn("{} {}: '{}'", INTERNAL_ERROR_MSG, " when song already exist", ex.getApiErrorModel().getErrorMessage());
        return new ResponseEntity<>(ex.getApiErrorModel(), ex.getHttpStatus());
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ApiErrorModel> handleFeignException(FeignException ex) {
        log.warn("{} {}: '{}'", INTERNAL_ERROR_MSG, " when feign clien execute", ex.getMessage());
        ApiErrorModel response = ApiErrorModelDetails.builder()
                .errorMessage(ex.contentUTF8())
                .errorCode(ex.status())
                .build();

        return new ResponseEntity<>(response, HttpStatusCode.valueOf(ex.status()));
    }

    @ExceptionHandler(CustomFeignApiException.class)
    public ResponseEntity<ApiErrorModel> handleCustomFeignApiException(CustomFeignApiException ex) {
        log.warn("{} {}: '{}'", INTERNAL_ERROR_MSG, " when feign clien execute", ex.getMessage());
        return new ResponseEntity<>(ex.getApiErrorModel(), ex.getHttpStatus());
    }

    @ExceptionHandler(CustomBadGatewayException.class)
    public ResponseEntity<ApiErrorModel> handleCustomBadGatewayException(CustomBadGatewayException ex) {
        log.warn("{} {}: '{}'", INTERNAL_ERROR_MSG, " when feign clien execute", ex.getMessage());
        return new ResponseEntity<>(ex.getApiErrorModel(), ex.getHttpStatus());
    }

    @ExceptionHandler(CustomFallbackException.class)
    public ResponseEntity<ApiErrorModel> handleCustomFallbackException(CustomFallbackException ex) {
        log.warn("{} {}: '{}'", INTERNAL_ERROR_MSG, " when feign clien execute", ex.getMessage());
        return new ResponseEntity<>(ex.getApiErrorModel(), ex.getHttpStatus());
    }
}
