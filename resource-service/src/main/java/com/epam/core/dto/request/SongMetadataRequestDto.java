package com.epam.core.dto.request;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode(exclude = "id")
public class SongMetadataRequestDto {

    private int id;
    private String name;
    private String artist;
    private String album;
    private String duration;
    private String year;
}
