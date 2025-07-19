package com.epam.core.service;

import com.epam.core.dto.request.SongMetadataRequestDto;
import com.epam.core.dto.response.DeletedByIdsResponseDto;
import com.epam.core.dto.response.UploadedSongResponseDto;
import com.epam.core.exception.AudioDataException;
import com.epam.core.exception.DeleteSongAndMetadataByIdsException;
import com.epam.core.exception.GetSongByIdException;
import com.epam.core.exception.ResourceDeletionException;
import com.epam.core.exception.SongAlreadyExistException;
import com.epam.core.extractor.impl.MetadataExtractorImpl;
import com.epam.core.util.AudioParserUtil;
import com.epam.data.entity.Song;
import com.epam.data.repository.ResourceRepository;
import com.epam.web.feign.SongServiceFeignClient;
import feign.FeignException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tika.metadata.Metadata;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
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
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ResourceService {

    ResourceRepository resourceRepository;
    SongServiceFeignClient songServiceFeignClient;
    MetadataExtractorImpl metadataExtractor;

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
    public DeletedByIdsResponseDto deleteSongsAndMetadataByIds(final String requestIds) {
        log.debug("Starting delete songs and metadata by ID's: '{}'", requestIds);

        validateRequestIds(requestIds);

        List<Integer> splittedIds = Arrays.stream(StringUtils.split(requestIds, ","))
                .map(StringUtils::strip)
                .map(Integer::parseInt)
                .toList();

        log.debug("Validated and parsed ID's: '{}'", splittedIds);
        List<Integer> idsForRemoving = resourceRepository.findExistingIds(splittedIds);

        if (CollectionUtils.isEmpty(idsForRemoving)) {
            log.warn("Restriction: Song with the specified ID's does not exist: '{}'", idsForRemoving);
        } else {
            String ids = idsForRemoving.stream()
                    .map(i -> Integer.toString(i))
                    .collect(Collectors.joining(", "));

            deleteMetadata(ids);

            log.debug("Deleting songs by ID's: '{}'", ids);
            resourceRepository.deleteAllById(idsForRemoving);
            log.info("Successfully deleted songs and metadata by ID's: '{}'", idsForRemoving);
        }
        return new DeletedByIdsResponseDto(idsForRemoving);
    }

    private void deleteMetadata(String requestIds) {
        try {
            log.debug("Deleting metadata by ID's: '{}'", requestIds);
            songServiceFeignClient.deleteMetadataByIds(requestIds);
            log.info("Successfully deleted metadata by ID's: '{}'", requestIds);
        } catch (FeignException ex) {
            log.error("FeignException occurred while deleting metadata by ID's '{}': {}", requestIds, ex.contentUTF8(), ex);
            throw new ResourceDeletionException("Failed to delete metadata by ID's: '%s'".formatted(requestIds));
        }
    }

    private void validateIdsForRemoving(List<Integer> idsForRemoving) {
        if (CollectionUtils.isEmpty(idsForRemoving)) {
            Map<String, String> errorDetails = Collections.singletonMap("Restriction",
                    "Song with the specified ID's does not exist: '{%s}'".formatted(idsForRemoving));
            log.error("Restriction: Song with the specified ID's does not exist: '{}'", idsForRemoving);
            throw new DeleteSongAndMetadataByIdsException(
                    "Song with the specified ID's: '%s' does not exist.".formatted(idsForRemoving), HttpStatus.NOT_FOUND);
        }
    }

    private void validateRequestIds(String requestIds) {
        if (StringUtils.isBlank(requestIds)) {
            Map<String, String> errorDetails = Map.of("Restriction", "CSV string cannot be empty.");
            log.error("Restriction: CSV string cannot be empty.");
            throw new DeleteSongAndMetadataByIdsException("CSV string cannot be empty.");
        }

        if (requestIds.length() > 200) {
            Map<String, String> errorDetails = Collections.singletonMap("Restriction",
                    "CSV string length must be less than 200 characters.");
            log.error("Restriction: CSV string length must be less than 200 characters. Actual length: '{}'", requestIds.length());
            throw new DeleteSongAndMetadataByIdsException("Actual length: '%d'. %s".formatted(requestIds.length(),
                    "CSV string length must be less than 200 characters."));
        }

        List<String> invalidIds = Arrays.stream(StringUtils.split(requestIds, ","))
                .map(StringUtils::strip)
                .filter(i -> !StringUtils.isNumeric(i))
                .toList();

        if (CollectionUtils.isNotEmpty(invalidIds)) {
            Map<String, String> errorDetails = Map.of("ID's: %s".formatted(invalidIds),
                    "The provided ID's is invalid (e.g., contains letters, decimals, is negative, or zero).");
            log.error("The provided ID's is invalid (e.g., contains letters, decimals, is negative, or zero): '{%s}'".formatted(invalidIds));
            throw new DeleteSongAndMetadataByIdsException("The provided ID's: '%s' is invalid (e.g., contains letters, decimals, is negative, or zero)."
                    .formatted(invalidIds));
        }
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS, isolation = Isolation.READ_COMMITTED)
    public Resource getSongById(final Integer requestId) {
        log.debug("Fetching song by ID: '{}'", requestId);

        validateRequestId(requestId);

        Song fetchedSong = fetchedSong(requestId);
        log.info("Successfully fetched song by ID' '{}'", requestId);

        return new ByteArrayResource(fetchedSong.getData());
    }

    private Song fetchedSong(Integer requestId) {
        return resourceRepository.findById(requestId)
                .orElseThrow(() -> {
                    log.error("Song with the specified ID '{}' does not exist.", requestId);
                    return new GetSongByIdException(
                            "Song with the specified ID '%s' does not exist".formatted(requestId), HttpStatus.NOT_FOUND);
                });
    }

    private void validateRequestId(Integer requestId) {
        if (Objects.isNull(requestId) || requestId <= 0) {
            Map<String, String> errorDetails = Map.of("ID: %s".formatted(requestId),
                    "The provided ID is invalid (e.g., contains letters, decimals, is negative, or zero).");
            log.error("The provided ID is invalid (e.g., contains letters, decimals, is negative, or zero): %s".formatted(requestId));
            throw new GetSongByIdException("The provided ID: '%s' is invalid (e.g., contains letters, decimals, is negative, or zero)."
                    .formatted(requestId));
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.REPEATABLE_READ)
    public UploadedSongResponseDto saveSongAndMetadata(final byte[] audioFile) {
        log.debug("Starting saving song and metadata.");

//        validateBinaryData(audioFile);
        if (audioFile == null || audioFile.length == 0) {
            return new UploadedSongResponseDto();
        }

        Metadata audioMetadata = AudioParserUtil.parseMp3File(audioFile);

        SongMetadataRequestDto songMetadataRequestDto = metadataExtractor.extractData(audioMetadata);

        Song rawSong = Song.builder().data(audioFile).build();

        validateSongIsExist(rawSong, songMetadataRequestDto);

        Song savedSong = resourceRepository.save(rawSong);
        songMetadataRequestDto.setId(savedSong.getId());

        saveMetadata(songMetadataRequestDto, savedSong.getId());

        return new UploadedSongResponseDto(savedSong.getId());
    }

    private void saveMetadata(SongMetadataRequestDto songMetadataRequestDto, Integer songId) {
        try {
            log.debug("Saving metadata for ID's': '{}'", songId);
            songServiceFeignClient.saveMetadata(songMetadataRequestDto);
            log.info("Successfully saved metadata for ID: '{}'", songId);
        } catch (FeignException ex) {
            log.error("Failed to save metadata for song ID '{}': {}", songId, ex.contentUTF8());
            Map<String, String> errorDetails = Map.of("ID: '{%d}'".formatted(songId), "Failed to save metadata for song.");
            throw new ResourceDeletionException("Failed to save metadata with resource ID: '%s'.".formatted(songId));
        }
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS, isolation = Isolation.READ_COMMITTED)
    private void validateSongIsExist(Song rawSong, SongMetadataRequestDto songMetadataRequestDto) {
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("data", ExampleMatcher.GenericPropertyMatcher::exact);
        Example<Song> songExample = Example.of(rawSong, matcher);
        boolean isExists = resourceRepository.exists(songExample);

        if (isExists) {
            String songInfo = String.format("Name: %s, Artist: %s",
                    songMetadataRequestDto.getName(), songMetadataRequestDto.getArtist());
            log.error("Duplicate song detected: '{}'", songInfo);
            Map<String, String> errorDetails = Map.of("Song info", songInfo);
            throw new SongAlreadyExistException("Song already exist with info: '%s'.".formatted(songInfo));
        }
    }

    private void validateBinaryData(byte[] audioFile) {
        if (audioFile == null || audioFile.length == 0) {
            log.error("Empty audio file provided.");
            throw new AudioDataException("Audio file cannot be empty.");
        }
    }
}