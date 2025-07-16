package com.epam.core.dto.response;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class SongAndMetadataResponseDto {
    SongResponseDto songResponseDto;
    SongMetadataResponseDto metadataResponseDto;
}
