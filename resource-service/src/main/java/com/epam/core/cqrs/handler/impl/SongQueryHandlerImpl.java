package com.epam.core.cqrs.handler.impl;

import com.epam.core.cqrs.handler.QueryHandler;
import com.epam.core.cqrs.query.FindByIdQuery;
import com.epam.core.cqrs.query.FindByIdsQuery;
import com.epam.core.dto.FindByIdDto;
import com.epam.core.dto.response.DeletedByIdsResponseDto;
import com.epam.core.exception.GetSongByIdException;
import com.epam.data.entity.Song;
import com.epam.data.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SongQueryHandlerImpl implements QueryHandler {

    private final ResourceRepository resourceRepository;
    private final ConversionService conversionService;

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, isolation = Isolation.REPEATABLE_READ, readOnly = true)
    public FindByIdDto findById(FindByIdQuery query) {
        int requestId = query.getId();

        Song fetchedSong = resourceRepository.findById(requestId)
                .orElseThrow(() -> {
                    log.error("Song with the specified ID '{}' does not exist.", requestId);
                    return new GetSongByIdException(
                            "Song with the specified ID '%s' does not exist".formatted(requestId), HttpStatus.NOT_FOUND);
                });

        return conversionService.convert(fetchedSong, FindByIdDto.class);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public DeletedByIdsResponseDto findByIds(FindByIdsQuery query) {
        List<Integer> existedIds = resourceRepository.findExistingIds(query.getIds());
        return new DeletedByIdsResponseDto(existedIds);
    }
}
