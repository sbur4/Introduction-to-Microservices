package com.epam.core.service;

import com.epam.core.dto.request.SongMetadataRequestDto;
import com.epam.core.dto.response.DeletedByIdsResponseDto;
import com.epam.core.dto.response.SongAndMetadataResponseDto;
import com.epam.core.dto.response.SongMetadataResponseDto;
import com.epam.core.dto.response.SongResponseDto;
import com.epam.core.dto.response.UploadedSongResponseDto;
import com.epam.core.exception.AudioDataException;
import com.epam.core.exception.DeleteSongAndMetadataByIdsException;
import com.epam.core.exception.GetSongAndMetadataByIdException;
import com.epam.core.exception.ResourceDeletionException;
import com.epam.core.exception.SongAlreadyExistException;
import com.epam.core.extractor.impl.MetadataExtractorImpl;
import com.epam.core.mapper.impl.SongAndMetadataMapperImpl;
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
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Example;
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
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ResourceService {

    static String IDS = "ids";
    static String RESTRICTION_CSV_STRING_CANNOT_BE_EMPTY = "Restriction: CSV string cannot be empty.";
    static String THE_PROVIDED_ID_S_IS_INVALID =
            "The provided ID's is invalid (e.g., contains letters, decimals, is negative, or zero).";
    static String RESTRICTION_CSV_STRING_LENGTH_MUST_BE_LESS_THAN_200_CHARACTERS =
            "Restriction: CSV string length must be less than 200 characters.";
    static String RESTRICTION_SONG_METADATA_WITH_THE_SPECIFIED_ID_S_DOES_NOT_EXIST =
            "Restriction: Song with the specified ID's does not exist.";
    static String LOG_INFO = ": '{}'";
    static String THE_PROVIDED_ID_IS_INVALID = "The provided ID is invalid (e.g., contains letters, decimals, is negative, or zero).";

    ResourceRepository resourceRepository;
    SongServiceFeignClient songServiceFeignClient;
    ConversionService conversionService;
    SongAndMetadataMapperImpl dataMapper;
    MetadataExtractorImpl metadataExtractor;

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
    public DeletedByIdsResponseDto deleteSongsAndMetadataByIds(final String requestIds) {
        log.debug("Starting delete songs and metadata by ID's: '{}'", requestIds);

        validateRequestIds(requestIds);

        List<Integer> splittedIds = Arrays.stream(StringUtils.split(requestIds, ","))
                .map(StringUtils::strip)
                .map(Integer::parseInt)
                .toList();

        validateSplittedIds(splittedIds);

        log.debug("Validated and parsed ID's: '{}'", splittedIds);
        List<Integer> idsForRemoving = resourceRepository.findExistingIds(splittedIds);

        validateIdsForRemoving(idsForRemoving);

        deleteMetadata(requestIds);

        log.debug("Deleting songs for ID's': '{}'", idsForRemoving);
        resourceRepository.deleteAllById(idsForRemoving);

        log.info("Successfully deleted songs and metadata for ID's: '{}'", idsForRemoving);
        return new DeletedByIdsResponseDto(idsForRemoving);
    }

    private void deleteMetadata(String requestIds) {
        try {
            log.debug("Deleting metadata for ID's': '{}'", requestIds);
            songServiceFeignClient.deleteMetadataByIds(requestIds);
            log.info("Successfully deleted metadata for ID's: '{}'", requestIds);
        } catch (FeignException ex) {
            log.error("FeignException occurred while deleting metadata for IDs '{}': {}", requestIds, ex.contentUTF8(), ex);
            Map<String, String> errorDetails = Map.of(IDS + ": '{" + requestIds + "}'", "Failed to delete metadata for IDs.");
            throw new ResourceDeletionException("Failed to delete metadata for IDs.", errorDetails);
        }
    }

    private void validateIdsForRemoving(List<Integer> idsForRemoving) {
        if (CollectionUtils.isEmpty(idsForRemoving)) {
            Map<String, String> errorDetails = Collections.singletonMap(IDS,
                    RESTRICTION_SONG_METADATA_WITH_THE_SPECIFIED_ID_S_DOES_NOT_EXIST);
            log.error(RESTRICTION_SONG_METADATA_WITH_THE_SPECIFIED_ID_S_DOES_NOT_EXIST + LOG_INFO, idsForRemoving);
            throw new DeleteSongAndMetadataByIdsException(HttpStatus.NOT_FOUND, errorDetails);
        }
    }

    private void validateSplittedIds(List<Integer> splittedIds) {
        if (splittedIds.size() > 200) {
            Map<String, String> errorDetails = Collections.singletonMap(IDS,
                    RESTRICTION_CSV_STRING_LENGTH_MUST_BE_LESS_THAN_200_CHARACTERS);
            log.error(RESTRICTION_CSV_STRING_LENGTH_MUST_BE_LESS_THAN_200_CHARACTERS + LOG_INFO, splittedIds);
            throw new DeleteSongAndMetadataByIdsException(errorDetails);
        }
    }

    private void validateRequestIds(String requestIds) {
        if (StringUtils.isBlank(requestIds)) {
            Map<String, String> errorDetails = Map.of(IDS, RESTRICTION_CSV_STRING_CANNOT_BE_EMPTY);
            log.error(RESTRICTION_CSV_STRING_CANNOT_BE_EMPTY);
            throw new DeleteSongAndMetadataByIdsException(errorDetails);
        }

        List<String> invalidIds = Arrays.stream(StringUtils.split(requestIds, ","))
                .map(StringUtils::strip)
                .filter(i -> !StringUtils.isNumeric(i))
                .toList();

        if (CollectionUtils.isNotEmpty(invalidIds)) {
            Map<String, String> errorDetails = Map.of(IDS + ": " + invalidIds, THE_PROVIDED_ID_S_IS_INVALID);
            log.error(THE_PROVIDED_ID_S_IS_INVALID + LOG_INFO, invalidIds);
            throw new DeleteSongAndMetadataByIdsException(errorDetails);
        }
    }

//    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS, isolation = Isolation.READ_COMMITTED)
    public SongAndMetadataResponseDto getSongAndMetadataById(final Integer requestId) {
        log.debug("Fetching song and metadata for ID: '{}'", requestId);

        validateRequestId(requestId);

        SongMetadataResponseDto metadataResponseDto = fetchMetadata(requestId);
        log.info("Successfully fetched metadata for ID' '{}'", requestId);

        Song fetchedSong = fetchedSong(requestId);

        SongResponseDto songResponseDto = conversionService.convert(fetchedSong, SongResponseDto.class);
        SongAndMetadataResponseDto songAndMetadataResponseDto = dataMapper.mapData(metadataResponseDto, songResponseDto);

        log.info("Successfully fetched song and metadata for ID: '{}'", requestId);
        return songAndMetadataResponseDto;
    }

    private Song fetchedSong(Integer requestId) {
        return resourceRepository.findById(requestId)
                .orElseThrow(() -> {
                    log.error("Song with the specified ID '{}' does not exist.", requestId);
                    return new GetSongAndMetadataByIdException(
                            "Song with the specified ID %s does not exist".formatted(requestId), HttpStatus.NOT_FOUND);
                });
    }

    private SongMetadataResponseDto fetchMetadata(Integer requestId) {
        try {
            log.debug("Fetching metadata for ID': '{}'", requestId);
            return songServiceFeignClient.getMetadataById(requestId);
        } catch (FeignException ex) {
            log.error("Failed to fetch metadata for ID '{}': {}", requestId, ex.contentUTF8(), ex);
            Map<String, String> errorDetails = Map.of("id" + ": '{" + requestId + "}'", "Failed to fetch metadata for ID.");
            throw new ResourceDeletionException("Failed to metadata metadata for ID.", errorDetails);
        }
    }

    private void validateRequestId(Integer requestId) {
        if (Objects.isNull(requestId) || requestId <= 0) {
            Map<String, String> errorDetails = Map.of("id", THE_PROVIDED_ID_IS_INVALID);
            log.error(THE_PROVIDED_ID_IS_INVALID);
            throw new GetSongAndMetadataByIdException(errorDetails);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.REPEATABLE_READ)
    public UploadedSongResponseDto saveSongAndMetadata(final byte[] audioFile) {
        log.debug("Starting saving song and metadata.");

        validateBinaryData(audioFile);

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
            Map<String, String> errorDetails = Map.of(IDS + ": '{" + songId + "}'", "Failed to save metadata for song.");
            throw new ResourceDeletionException("Failed to save metadata.", errorDetails);
        }
    }

    private void validateBinaryData(byte[] audioFile) {
        if (audioFile == null || audioFile.length == 0) {
            log.error("Empty audio file provided.");
            throw new AudioDataException("Audio file cannot be empty.");
        }
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS, isolation = Isolation.READ_COMMITTED)
    private void validateSongIsExist(Song rawSong, SongMetadataRequestDto songMetadataRequestDto) {
        Example<Song> songExample = Example.of(rawSong);
        boolean isExists = resourceRepository.exists(songExample);

        if (isExists) {
            String songInfo = String.format("Name: %s, Artist: %s",
                    songMetadataRequestDto.getName(), songMetadataRequestDto.getArtist());
            log.error("Duplicate song detected: {}", songInfo);
            Map<String, String> errorDetails = Map.of("Song info", songInfo);
            throw new SongAlreadyExistException("Song already exists.", errorDetails);
        }
    }
}