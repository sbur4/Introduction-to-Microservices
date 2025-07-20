package com.epam.web.feign.fallback;

import com.epam.core.exception.CustomBadGatewayException;
import com.epam.core.exception.CustomFallbackException;
import com.epam.core.exception.CustomFeignApiException;
import com.epam.core.exception.core.BaseRuntimeException;
import com.epam.core.exception.model.ApiErrorModel;
import com.epam.core.exception.model.ApiErrorModelDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Getter
@AllArgsConstructor
public abstract class MicroserviceFallback {

    private static final int FEIGN_UNKNOWN_STATUS = NumberUtils.INTEGER_MINUS_ONE;

    private final Exception exception;
    private final ObjectMapper objectMapper;

    protected BaseRuntimeException throwException(final String message) {
        FeignException feignException = getFeignExceptionOrFallback(message);
        return mapToSpecificException(feignException, message);
    }

    private FeignException getFeignExceptionOrFallback(final String message) {
        if (exception instanceof FeignException) {
            return (FeignException) exception;
        }
        log.error("Exception is not a FeignException. Falling back with message: {}", message);
        throw new CustomFallbackException(message);
    }

    private BaseRuntimeException mapToSpecificException(FeignException feignException, final String message) {
        int statusCode = feignException.status();

        if (statusCode == FEIGN_UNKNOWN_STATUS) {
            log.warn("FeignException encountered with unknown status: {}", message);
            return new CustomFallbackException(message);
        }

        if (isClientError(statusCode)) {
            return handleClientError(feignException);
        }

        if (isServerError(statusCode)) {
            log.error("Server error encountered ({}): {}", statusCode, feignException.getMessage());
            return new CustomBadGatewayException(message);
        }

        log.error("Unhandled exception status {}: {}", statusCode, message);
        return new BaseRuntimeException(message);
    }

    private CustomFeignApiException handleClientError(FeignException feignException) {
        Optional<ApiErrorModel> apiErrorModelOptional = parseFeignExceptionContent(feignException);

        if (apiErrorModelOptional.isPresent()) {
            ApiErrorModel apiErrorModel = apiErrorModelOptional.get();
            log.error("Client error with response body: {}", apiErrorModel);
            return new CustomFeignApiException(apiErrorModel, feignException.status());
        }

        log.warn("Failed to parse FeignException content. Falling back to generic exception.");
        throw new BaseRuntimeException("Cannot parse FeignException content for client error.");
    }

    private Optional<ApiErrorModel> parseFeignExceptionContent(final FeignException feignException) {
        try {
            String content = feignException.contentUTF8();
            return Optional.ofNullable(objectMapper.readValue(content, ApiErrorModelDetails.class));
        } catch (IOException ex) {
            log.error("Error parsing FeignException content into ApiErrorModel", ex);
        }
        return Optional.empty();
    }

    private boolean isServerError(int status) {
        return status >= HttpStatus.INTERNAL_SERVER_ERROR.value();
    }

    private boolean isClientError(int status) {
        return HttpStatus.BAD_REQUEST.value() <= status && status < HttpStatus.INTERNAL_SERVER_ERROR.value();
    }
}
