package com.epam.core.dto.response;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class SongMetadataResponseDto {
    int id;
    String name;
    String artist;
    String album;
    String duration;
    String year;
}
