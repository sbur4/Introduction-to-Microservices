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
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class SongMetadataService {

    static String IDS = "ids";
    static String RESTRICTION_CSV_STRING_CANNOT_BE_EMPTY = "Restriction: CSV string cannot be empty.";
    static String THE_PROVIDED_ID_S_IS_INVALID =
            "The provided ID's is invalid (e.g., contains letters, decimals, is negative, or zero).";
    static String RESTRICTION_CSV_STRING_LENGTH_MUST_BE_LESS_THAN_200_CHARACTERS =
            "Restriction: CSV string length must be less than 200 characters.";
    static String RESTRICTION_SONG_METADATA_WITH_THE_SPECIFIED_ID_S_DOES_NOT_EXIST =
            "Restriction: Song metadata with the specified ID's does not exist.";
    static String LOG_INFO = ": '{}'";

    SongMetadataRepository metadataRepository;
    ConversionService conversionService;

    public DeletedByIdsResponseDto deleteMetadataByIds(final String requestIds) {
        log.debug("Starting delete metadata by ID's: '{}'", requestIds);

        validateRequestIds(requestIds);

        List<Integer> splittedIds = Arrays.stream(StringUtils.split(requestIds, ","))
                .map(StringUtils::strip)
                .map(Integer::parseInt)
                .toList();

        validateSplittedIds(splittedIds);

        log.debug("Validated and parsed ID's: '{}'", splittedIds);
        List<Integer> idsForRemoving = metadataRepository.findByResourceIdIn(splittedIds);

        validateIdsForRemoving(idsForRemoving);

        log.debug("Deleting metadata for ID's': '{}'", idsForRemoving);
        metadataRepository.deleteAllById(idsForRemoving);

        log.info("Successfully deleted metadata for ID's: '{}'", idsForRemoving);
        return new DeletedByIdsResponseDto(idsForRemoving);
    }

    private void validateIdsForRemoving(List<Integer> idsForRemoving) {
        if (CollectionUtils.isEmpty(idsForRemoving)) {
            Map<String, String> errorDetails = Collections.singletonMap(IDS,
                    RESTRICTION_SONG_METADATA_WITH_THE_SPECIFIED_ID_S_DOES_NOT_EXIST);
            log.error(RESTRICTION_SONG_METADATA_WITH_THE_SPECIFIED_ID_S_DOES_NOT_EXIST + LOG_INFO, idsForRemoving);
            throw new DeleteMetadataByIdsException(HttpStatus.NOT_FOUND, errorDetails);
        }
    }

    private void validateSplittedIds(List<Integer> splittedIds) {
        if (splittedIds.size() > 200) {
            Map<String, String> errorDetails = Collections.singletonMap(IDS,
                    RESTRICTION_CSV_STRING_LENGTH_MUST_BE_LESS_THAN_200_CHARACTERS);
            log.error(RESTRICTION_CSV_STRING_LENGTH_MUST_BE_LESS_THAN_200_CHARACTERS + LOG_INFO, splittedIds);
            throw new DeleteMetadataByIdsException(errorDetails);
        }
    }

    private void validateRequestIds(String requestIds) {
        if (StringUtils.isBlank(requestIds)) {
            Map<String, String> errorDetails = Map.of(IDS, RESTRICTION_CSV_STRING_CANNOT_BE_EMPTY);
            log.error(RESTRICTION_CSV_STRING_CANNOT_BE_EMPTY);
            throw new DeleteMetadataByIdsException(errorDetails);
        }

        List<String> invalidIds = Arrays.stream(StringUtils.split(requestIds, ","))
                .map(StringUtils::strip)
                .filter(i -> !StringUtils.isNumeric(i))
                .toList();

        if (CollectionUtils.isNotEmpty(invalidIds)) {
            Map<String, String> errorDetails = Map.of(IDS + ": " + invalidIds, THE_PROVIDED_ID_S_IS_INVALID);
            log.error(THE_PROVIDED_ID_S_IS_INVALID + LOG_INFO, invalidIds);
            throw new DeleteMetadataByIdsException(errorDetails);
        }
    }

    public SongMetadataResponseDto getMetadataById(final Integer requestId) {
        log.debug("Fetching song metadata for ID: '{}'", requestId);

        validateRequestId(requestId);

        SongMetadata fetchedSongMetadata = metadataRepository.findById(requestId)
                .orElseThrow(() -> {
                    log.error("Song metadata with the specified ID '{}' does not exist.", requestId);
                    return new GetMetadataByIdException(
                            "Song metadata with the specified ID %s does not exist".formatted(requestId), HttpStatus.NOT_FOUND);
                });

        SongMetadataResponseDto responseDto = conversionService.convert(fetchedSongMetadata, SongMetadataResponseDto.class);

        log.info("Successfully fetched song metadata for ID: '{}'", requestId);
        return responseDto;
    }

    private void validateRequestId(Integer requestId) {
        if (Objects.isNull(requestId) || requestId <= 0) {
            Map<String, String> errorDetails = Map.of("id",
                    "The provided ID is invalid (e.g., contains letters, decimals, is negative, or zero).");
            log.error("The provided ID is invalid (e.g., contains letters, decimals, is negative, or zero).");
            throw new GetMetadataByIdException(errorDetails);
        }
    }

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
            throw new MetadataAlreadyExistException("Metadata already exists for the given ID.", errorDetails);
        }
    }
}
