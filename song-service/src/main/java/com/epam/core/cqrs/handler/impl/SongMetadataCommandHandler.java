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
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SongMetadataCommandHandler implements CommandHandler {

    private final SongMetadataRepository metadataRepository;
    private final ConversionService conversionService;

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public SongMetadataIdResponseDto saveMetadata(final SaveEntityCommand command) {
        SongMetadata newMetadata = conversionService.convert(command, SongMetadata.class);

        SongMetadata createdMetadata = metadataRepository.save(newMetadata);
        log.info("Successfully saving song metadata for ID: '{}'", createdMetadata.getId());

        return new SongMetadataIdResponseDto(createdMetadata.getId());
    }

    @Override
    @Transactional
    public void deleteByIds(final DeleteByIdsCommand command) {
        metadataRepository.deleteByIdIn(command.getIds());
    }
}
