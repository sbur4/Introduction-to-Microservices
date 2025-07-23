package com.epam.core.converter;

import com.epam.core.dto.SaveEntityDto;
import com.epam.core.util.ChecksumUtil;
import com.epam.data.entity.Song;
import lombok.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class RequestDtoToCommandDtoConverter implements Converter<Song, SaveEntityDto> {

    @Override
    public SaveEntityDto convert(@NonNull Song rawEntity) {
        byte[] data = rawEntity.getData();

        return SaveEntityDto.builder()
                .data(Objects.requireNonNull(data))
                .checksum(Objects.requireNonNull(ChecksumUtil.generateChecksum(data)))
                .build();


    }
}
