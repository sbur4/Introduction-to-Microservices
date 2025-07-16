package com.epam.core.mapper.impl;

import com.epam.core.dto.response.SongAndMetadataResponseDto;
import com.epam.core.dto.response.SongMetadataResponseDto;
import com.epam.core.dto.response.SongResponseDto;
import com.epam.core.mapper.Mapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Component
public class SongAndMetadataMapperImpl implements Mapper<SongAndMetadataResponseDto> {

    @Override
    public SongAndMetadataResponseDto mapData(Object... objs) {
        log.debug("Starting to map SongAndMetadataResponseDto from input arguments: {}", (Object) objs);

        validateInputs(objs);

        SongMetadataResponseDto metadataResponseDto = (SongMetadataResponseDto) objs[0];
        SongResponseDto songResponseDto = (SongResponseDto) objs[1];

        return SongAndMetadataResponseDto.builder()
                .songResponseDto(Objects.requireNonNull(songResponseDto))
                .metadataResponseDto(Objects.requireNonNull(metadataResponseDto))
                .build();
    }

    private void validateInputs(Object[] objs) {
        if (!(objs[0] instanceof SongMetadataResponseDto)) {
            String errorMessage = "Invalid argument: First object is not of type SongMetadataResponseDto.";
            log.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        if (!(objs[1] instanceof SongResponseDto)) {
            String errorMessage = "Invalid argument: Second object is not of type SongResponseDto.";
            log.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        log.debug("Validation passed for input arguments: SongMetadataResponseDto={}, SongResponseDto={}",
                objs[0], objs[1]);
    }
}
