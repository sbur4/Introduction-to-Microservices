package com.epam.core.converter;

import com.epam.core.dto.FindByIdDto;
import com.epam.data.entity.Song;
import lombok.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class GetSongEntityToResponseDtoConverter implements Converter<Song, FindByIdDto> {

    @Override
    public FindByIdDto convert(@NonNull Song fetchedSong) {
        return FindByIdDto.builder()
                .id(Objects.requireNonNull(fetchedSong.getId()))
                .data(Objects.requireNonNull(fetchedSong.getData()))
                .build();
    }
}
