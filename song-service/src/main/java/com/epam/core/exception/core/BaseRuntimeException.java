package com.epam.core.exception.core;

import com.epam.core.exception.model.ApiErrorModel;
import com.epam.core.exception.model.ApiErrorModelDetails;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.http.HttpStatus;

import java.util.Map;
import java.util.function.Function;

@Getter
public class BaseRuntimeException extends RuntimeException implements HttpStatusException {

    private static HttpStatus HTTP_STATUS = HttpStatus.INTERNAL_SERVER_ERROR;
    private static final Function<Integer, HttpStatus> httpStatusResolver = errorCode -> HttpStatus.resolve(errorCode);

    private final ApiErrorModel apiErrorModel;

    public BaseRuntimeException(@NonNull String message) {
        super(message);
        this.apiErrorModel = ApiErrorModel.builder()
                .errorMessage(message)
                .errorCode(HTTP_STATUS.value())
                .build();
    }

    public BaseRuntimeException(@NonNull final ApiErrorModel apiErrorModel) {
        this.apiErrorModel = apiErrorModel;
        HTTP_STATUS = HttpStatus.resolve(apiErrorModel.getErrorCode());
    }

    public BaseRuntimeException(@NonNull String message, int errorCode) {
        super(message);
        HTTP_STATUS = httpStatusResolver.apply(errorCode);
        this.apiErrorModel = ApiErrorModel.builder()
                .errorMessage(message)
                .errorCode(errorCode)
                .build();
    }

    public BaseRuntimeException(@NonNull String message, @NonNull HttpStatus errorCode) {
        super(message);
        HTTP_STATUS = errorCode;
        this.apiErrorModel = ApiErrorModel.builder()
                .errorMessage(message)
                .errorCode(errorCode.value())
                .build();
    }

    public BaseRuntimeException(@NonNull String message, @NonNull HttpStatus errorCode, @NonNull Map<String, String> errorDetails) {
        super(message);
        HTTP_STATUS = errorCode;
        this.apiErrorModel = ApiErrorModelDetails.builder()
                .errorMessage(message)
                .errorCode(errorCode.value())
                .errorDetails(errorDetails)
                .build();
    }

    public BaseRuntimeException(@NonNull String message, int errorCode, @NonNull Map<String, String> errorDetails) {
        super(message);
        HTTP_STATUS = httpStatusResolver.apply(errorCode);
        this.apiErrorModel = ApiErrorModelDetails.builder()
                .errorMessage(message)
                .errorCode(errorCode)
                .errorDetails(errorDetails)
                .build();
    }

    public BaseRuntimeException(@NonNull String message, @NonNull Map<String, String> errorDetails) {
        super(message);
        this.apiErrorModel = ApiErrorModelDetails.builder()
                .errorMessage(message)
                .errorCode(HTTP_STATUS.value())
                .errorDetails(errorDetails)
                .build();
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HTTP_STATUS;
    }
}
