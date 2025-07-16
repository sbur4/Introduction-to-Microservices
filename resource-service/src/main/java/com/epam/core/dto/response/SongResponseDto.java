package com.epam.core.dto.response;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class SongResponseDto {
    int id;
    byte[] data;
}
