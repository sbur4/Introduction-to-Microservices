package com.epam.core.exception.core;

import org.springframework.http.HttpStatus;

@FunctionalInterface
public interface HttpStatusException {

    HttpStatus getHttpStatus();
}
