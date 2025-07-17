package com.epam.core.service;

import com.epam.core.dto.request.SongMetadataRequestDto;
import com.epam.core.dto.response.DeletedByIdsResponseDto;
import com.epam.core.dto.response.SongMetadataIdResponseDto;
import com.epam.core.dto.response.SongMetadataResponseDto;
import com.epam.core.exception.DeleteMetadataByIdsException;
import com.epam.core.exception.GetMetadataByIdException;
import com.epam.core.exception.MetadataAlreadyExistException;
import com.epam.data.entity.SongMetadata;
import com.epam.data.repository.SongMetadataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.SUPPORTS)
public class SongMetadataService {

    private static final String RESTRICTION = "Restriction";

    private final SongMetadataRepository metadataRepository;
    private final ConversionService conversionService;

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE, timeout = 30,
            rollbackFor = DeleteMetadataByIdsException.class
    )
    public DeletedByIdsResponseDto deleteMetadataByIds(final String requestIds) {
        log.debug("Starting delete metadata by ID's: '{}'", requestIds);

        validateRequestIds(requestIds);

        List<Integer> splittedIds = Arrays.stream(StringUtils.split(requestIds, ","))
                .map(StringUtils::strip)
                .map(Integer::parseInt)
                .toList();

        log.debug("Validated and parsed ID's: '{}'", splittedIds);
        List<Integer> idsForRemoving = metadataRepository.findByIdIn(splittedIds);

//        validateIdsForRemoving(idsForRemoving);

        log.debug("Deleting metadata by ID's': '{}'", idsForRemoving);
        metadataRepository.deleteAllById(idsForRemoving);

        log.info("Successfully deleted metadata by ID's: '{}'", idsForRemoving);
        return new DeletedByIdsResponseDto(idsForRemoving);
    }

    private void validateIdsForRemoving(List<Integer> idsForRemoving) {
        if (CollectionUtils.isEmpty(idsForRemoving)) {
            Map<String, String> errorDetails = Collections.singletonMap(RESTRICTION,
                    "Song metadata with the specified ID's does not exist.");
            log.error("Restriction: Song metadata with the specified ID's does not exist: '{}'", idsForRemoving);
            throw new DeleteMetadataByIdsException(HttpStatus.NOT_FOUND, errorDetails);
        }
    }

    private void validateRequestIds(String requestIds) {
        if (StringUtils.isBlank(requestIds)) {
            Map<String, String> errorDetails = Map.of(RESTRICTION, "CSV string cannot be empty.");
            log.error("Restriction: CSV string cannot be empty.");
            throw new DeleteMetadataByIdsException("Validation failure", errorDetails);
        }

        if (requestIds.length() > 200) {
            Map<String, String> errorDetails = Collections.singletonMap(RESTRICTION,
                    "CSV string length must be less than 200 characters.");
            log.error("Restriction: CSV string length must be less than 200 characters. Actual length: '{}'", requestIds.length());
            throw new DeleteMetadataByIdsException("Actual length: '{%d}'".formatted(requestIds.length()), errorDetails);
        }

        List<String> invalidIds = Arrays.stream(StringUtils.split(requestIds, ","))
                .map(StringUtils::strip)
                .filter(i -> !StringUtils.isNumeric(i))
                .toList();

        if (CollectionUtils.isNotEmpty(invalidIds)) {
            Map<String, String> errorDetails = Map.of("ids: " + invalidIds,
                    "The provided ID's is invalid (e.g., contains letters, decimals, is negative, or zero).");
            log.error("The provided ID's is invalid (e.g., contains letters, decimals, is negative, or zero): '{}'", invalidIds);
            throw new DeleteMetadataByIdsException(errorDetails);
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS, isolation = Isolation.READ_COMMITTED, readOnly = true)
    public SongMetadataResponseDto getMetadataById(final Integer requestId) {
        log.debug("Fetching song metadata for ID: '{}'", requestId);

        validateRequestId(requestId);

        SongMetadata fetchedSongMetadata = metadataRepository.findById(requestId)
                .orElseThrow(() -> {
                    log.error("Song metadata with the specified ID '{}' does not exist.", requestId);
                    return new GetMetadataByIdException(
                            "Song metadata with the specified ID '%s' does not exist".formatted(requestId), HttpStatus.NOT_FOUND);
                });

        SongMetadataResponseDto responseDto = conversionService.convert(fetchedSongMetadata, SongMetadataResponseDto.class);

        log.info("Successfully fetched song metadata for ID: '{}'", requestId);
        return responseDto;
    }

    private void validateRequestId(Integer requestId) {
        if (Objects.isNull(requestId) || requestId <= 0) {
            Map<String, String> errorDetails = Map.of(RESTRICTION,
                    "The provided ID is invalid (e.g., contains letters, decimals, is negative, or zero).");
            log.error("The provided ID is invalid (e.g., contains letters, decimals, is negative, or zero).");
            throw new GetMetadataByIdException(errorDetails);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.REPEATABLE_READ, timeout = 10,
            rollbackFor = MetadataAlreadyExistException.class
    )
    public SongMetadataIdResponseDto saveMetadata(SongMetadataRequestDto requestDto) {
        log.debug("Starting saving metadata with ID: '{}'", requestDto.getId());

        validateRequestDto(requestDto);

        SongMetadata newMetadata = conversionService.convert(requestDto, SongMetadata.class);

        SongMetadata createdMetadata = metadataRepository.save(newMetadata);
        log.info("Successfully saving song metadata for ID: '{}'", createdMetadata.getId());

        return new SongMetadataIdResponseDto(createdMetadata.getId());
    }

    private void validateRequestDto(SongMetadataRequestDto requestDto) {
        boolean isMetadataExist = metadataRepository.findById(requestDto.getId()).isPresent();
        if (isMetadataExist) {
            Map<String, String> errorDetails = Map.of("id: '{%d}'".formatted(requestDto.getId()),
                    "Metadata for this ID already exists.");
            log.error("Metadata existence check for ID '{}': '{}'", requestDto.getId(), isMetadataExist);
            throw new MetadataAlreadyExistException("Metadata already exists for the given ID: '%d'".formatted(requestDto.getId()), errorDetails);
        }
    }
}
