package com.epam.web.model;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class ApiErrorResponse {

    private final String errorMessage;
    private final int errorCode;
}
