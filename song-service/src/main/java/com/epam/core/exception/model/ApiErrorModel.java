package com.epam.core.exception.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiErrorModel {

    @JsonProperty("errorMessage")
    private String errorMessage;

    @JsonProperty("errorCode")
    private int errorCode;
}
