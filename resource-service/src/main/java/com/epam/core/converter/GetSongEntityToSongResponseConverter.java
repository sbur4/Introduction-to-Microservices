package com.epam.core.converter;

import com.epam.core.dto.response.SongResponseDto;
import com.epam.data.entity.Song;
import lombok.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class GetSongEntityToSongResponseConverter implements Converter<Song, SongResponseDto> {

    @Override
    public SongResponseDto convert(@NonNull Song fetchedSong) {
        return SongResponseDto.builder()
                .id(fetchedSong.getId())
                .data(fetchedSong.getData())
                .build();
    }
}
