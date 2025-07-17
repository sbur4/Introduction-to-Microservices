package com.epam.core.exception.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.Map;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class ApiErrorResponseDetails extends ApiErrorResponse {

    private Map<String, String> errorDetails;
}
