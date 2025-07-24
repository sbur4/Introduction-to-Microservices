package com.epam.core.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class SaveEntityDto {

    int id;
    byte[] data;
}
