package com.epam.core.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class FindByIdDto {

    int id;
    byte[] data;
}
