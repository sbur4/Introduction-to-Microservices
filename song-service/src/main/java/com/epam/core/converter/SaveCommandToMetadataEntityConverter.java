package com.epam.core.converter;

import com.epam.core.cqrs.command.SaveEntityCommand;
import com.epam.data.entity.SongMetadata;
import lombok.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class SaveCommandToMetadataEntityConverter implements Converter<SaveEntityCommand, SongMetadata> {

    @Override
    public SongMetadata convert(@NonNull SaveEntityCommand entityCommand) {
        return SongMetadata.builder()
                .resourceId(Objects.requireNonNull(Integer.valueOf(entityCommand.getResourceId())))
                .name(Objects.requireNonNull(entityCommand.getName()))
                .artist(Objects.requireNonNull(entityCommand.getArtist()))
                .album(Objects.requireNonNull(entityCommand.getAlbum()))
                .duration(Objects.requireNonNull(entityCommand.getDuration()))
                .year(Objects.requireNonNull(entityCommand.getYear()))
                .build();
    }
}
