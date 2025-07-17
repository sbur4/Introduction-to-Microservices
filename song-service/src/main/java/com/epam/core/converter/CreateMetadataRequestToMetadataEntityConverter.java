package com.epam.core.converter;

import com.epam.core.dto.request.SongMetadataRequestDto;
import com.epam.data.entity.SongMetadata;
import lombok.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class CreateMetadataRequestToMetadataEntityConverter implements Converter<SongMetadataRequestDto, SongMetadata> {

    @Override
    public SongMetadata convert(@NonNull SongMetadataRequestDto requestDto) {
        return SongMetadata.builder()
                .resourceId(Objects.requireNonNull(Integer.valueOf(requestDto.getId())))
                .name(Objects.requireNonNull(requestDto.getName()))
                .artist(Objects.requireNonNull(requestDto.getArtist()))
                .album(Objects.requireNonNull(requestDto.getAlbum()))
                .duration(Objects.requireNonNull(requestDto.getDuration()))
                .year(Objects.requireNonNull(requestDto.getYear()))
                .build();
    }
}
