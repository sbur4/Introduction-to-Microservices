package com.epam.core.cqrs.handler.impl;

import com.epam.core.cqrs.handler.QueryHandler;
import com.epam.core.cqrs.query.FindByIdQuery;
import com.epam.core.cqrs.query.FindByIdsQuery;
import com.epam.core.dto.response.DeletedByIdsResponseDto;
import com.epam.core.dto.response.SongMetadataResponseDto;
import com.epam.core.exception.GetMetadataByIdException;
import com.epam.data.entity.SongMetadata;
import com.epam.data.repository.SongMetadataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SongMetadataQueryHandler implements QueryHandler {

    private final SongMetadataRepository metadataRepository;
    private final ConversionService conversionService;

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, isolation = Isolation.REPEATABLE_READ, readOnly = true)
    public SongMetadataResponseDto findById(final FindByIdQuery query) {

        int requestId = query.getId();
        SongMetadata fetchedSongMetadata = getSongMetadata(requestId)
                .orElseThrow(() -> {
                    log.error("Song metadata with the specified ID '{}' does not exist.", requestId);
                    return new GetMetadataByIdException(
                            "Song metadata with the specified ID '%s' does not exist".formatted(requestId), HttpStatus.NOT_FOUND);
                });

        return conversionService.convert(fetchedSongMetadata, SongMetadataResponseDto.class);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public DeletedByIdsResponseDto findByIds(final FindByIdsQuery query) {
        List<Integer> idsForRemoving = metadataRepository.findExistingIdsByResourceIdIn(query.getIds());
        return new DeletedByIdsResponseDto(idsForRemoving);
    }

    @Override
    public boolean isExistByResourceId(final FindByIdQuery query) {
        return getSongMetadata(query.getId())
                .isPresent();
    }

    @Transactional
    private Optional<SongMetadata> getSongMetadata(Integer requestId) {
        return metadataRepository.findByResourceId(requestId);
    }
}
