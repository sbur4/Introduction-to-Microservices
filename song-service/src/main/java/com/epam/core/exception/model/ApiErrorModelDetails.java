package com.epam.core.exception.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Singular;
import lombok.experimental.SuperBuilder;

import java.util.Map;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class ApiErrorModelDetails extends ApiErrorModel {

    @JsonProperty("details")
    @Singular
    private Map<String, String> errorDetails;
}
