package com.epam.core.exception;

import com.epam.core.exception.core.BaseRuntimeException;
import org.springframework.http.HttpStatus;

public class AudioDataException extends BaseRuntimeException {

    private static final int errorHttpStatusCode = HttpStatus.OK.value();

    public AudioDataException(String errorMessage) {
        super(errorMessage, errorHttpStatusCode);
    }
}
