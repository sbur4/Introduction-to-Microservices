package com.epam.core.cqrs.handler.impl;

import com.epam.core.cqrs.command.DeleteByIdsCommand;
import com.epam.core.cqrs.command.SaveEntityCommand;
import com.epam.core.cqrs.handler.CommandHandler;
import com.epam.core.dto.response.SongMetadataIdResponseDto;
import com.epam.data.entity.SongMetadata;
import com.epam.data.repository.SongMetadataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SongMetadataCommandHandler implements CommandHandler {

    private final SongMetadataRepository metadataRepository;
    private final ConversionService conversionService;

    @Override
    @Transactional
    public SongMetadataIdResponseDto saveMetadata(final SaveEntityCommand command) {
        SongMetadata newMetadata = conversionService.convert(command, SongMetadata.class);

        SongMetadata createdMetadata = metadataRepository.save(newMetadata);
        int createdId = createdMetadata.getId();
        log.info("Successfully saving song metadata for ID: '{}'", createdId);

        return new SongMetadataIdResponseDto(createdId);
    }

    @Override
    @Transactional
    public void deleteByIds(final DeleteByIdsCommand command) {
        List<Integer> idsForRemoving = command.getIds();
        metadataRepository.deleteByIdIn(idsForRemoving);
        log.info("Successfully deleted metadata by ID's: '{}'", idsForRemoving);
    }
}
