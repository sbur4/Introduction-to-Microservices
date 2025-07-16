package com.epam.core.converter;

import com.epam.core.dto.response.SongMetadataResponseDto;
import com.epam.data.entity.SongMetadata;
import lombok.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class GetMetadataEntityToMetadataResponseConverter implements Converter<SongMetadata, SongMetadataResponseDto> {

    @Override
    public SongMetadataResponseDto convert(@NonNull SongMetadata fetchedSongMetadata) {
        return SongMetadataResponseDto.builder()
                .resourceId(Objects.requireNonNull(fetchedSongMetadata.getResourceId()))
                .trackNumber(Objects.requireNonNull(fetchedSongMetadata.getTrackNumber()))
                .name(Objects.requireNonNull(fetchedSongMetadata.getName()))
                .artist(Objects.requireNonNull(fetchedSongMetadata.getArtist()))
                .album(Objects.requireNonNull(fetchedSongMetadata.getAlbum()))
                .duration(Objects.requireNonNull(fetchedSongMetadata.getDuration()))
                .year(Objects.requireNonNull(fetchedSongMetadata.getYear()))
                .build();
    }
}
