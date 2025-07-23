package com.epam.core.service;

import com.epam.core.converter.RequestDtoToCommandDtoConverter;
import com.epam.core.cqrs.command.DeleteByIdsCommand;
import com.epam.core.cqrs.handler.CommandHandler;
import com.epam.core.cqrs.handler.QueryHandler;
import com.epam.core.cqrs.query.FindByIdQuery;
import com.epam.core.cqrs.query.FindByIdsQuery;
import com.epam.core.dto.FindByIdDto;
import com.epam.core.dto.request.SongMetadataRequestDto;
import com.epam.core.dto.response.DeletedByIdsResponseDto;
import com.epam.core.dto.response.UploadedSongResponseDto;
import com.epam.core.exception.DeleteSongAndMetadataByIdsException;
import com.epam.core.exception.GetSongByIdException;
import com.epam.core.exception.ResourceDeletionException;
import com.epam.core.exception.SongAlreadyExistException;
import com.epam.core.extractor.impl.MetadataExtractorImpl;
import com.epam.core.util.AudioParserUtil;
import com.epam.data.entity.Song;
import com.epam.data.repository.ResourceRepository;
import com.epam.web.feign.client.SongServiceFeignClient;
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
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

// INFO: SAGA design pattern
@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ResourceService {

    private final CommandHandler commandHandler;
    private final QueryHandler queryHandler;
    //
    ResourceRepository resourceRepository;
    SongServiceFeignClient songServiceFeignClient;
    MetadataExtractorImpl metadataExtractor;

    public DeletedByIdsResponseDto deleteSongsAndMetadataByIds(final String requestIds) {
        log.debug("Starting delete songs and metadata by ID's: '{}'", requestIds);

        validateRequestIds(requestIds);

        List<Integer> splittedIds = splitIds(requestIds);

        log.debug("Validated and parsed ID's: '{}'", splittedIds);
        DeletedByIdsResponseDto responseDto = queryHandler.findByIds(new FindByIdsQuery(splittedIds));
        List<Integer> idsForRemoving = responseDto.getIds();

        if (CollectionUtils.isEmpty(idsForRemoving)) {
            log.warn("Restriction: Song with the specified ID's does not exist: '{}'", idsForRemoving);
        } else {
            String ids = buildRequest(idsForRemoving);

            deleteMetadata(ids);

            log.debug("Deleting songs by ID's: '{}'", ids);
            commandHandler.deleteByIds(new DeleteByIdsCommand(idsForRemoving));
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
            throw new ResourceDeletionException("Failed to delete metadata by ID's: '%s'.".formatted(requestIds));
        }
    }

    private String buildRequest(List<Integer> idsForRemoving) {
        return idsForRemoving.stream()
                .map(i -> Integer.toString(i))
                .collect(Collectors.joining(", "));
    }

    private List<Integer> splitIds(String requestIds) {
        return Arrays.stream(StringUtils.split(requestIds, ","))
                .map(StringUtils::strip)
                .map(Integer::parseInt)
                .toList();
    }

    private void validateRequestIds(String requestIds) {
        if (StringUtils.isBlank(requestIds)) {
            log.error("Restriction: CSV string cannot be empty.");
            throw new DeleteSongAndMetadataByIdsException("CSV string cannot be empty.");
        }

        if (requestIds.length() > 200) {
            log.error("Restriction: CSV string length must be less than 200 characters. Actual length: '{}'", requestIds.length());
            throw new DeleteSongAndMetadataByIdsException("Actual length: '%d'. %s.".formatted(requestIds.length(),
                    "CSV string length must be less than 200 characters."));
        }

        List<String> invalidIds = Arrays.stream(StringUtils.split(requestIds, ","))
                .map(StringUtils::strip)
                .filter(i -> !StringUtils.isNumeric(i))
                .toList();

        if (CollectionUtils.isNotEmpty(invalidIds)) {

            log.error("The provided ID's '{}' is invalid (e.g., contains letters, decimals, is negative, or zero).", invalidIds);
            throw new DeleteSongAndMetadataByIdsException("The provided ID's: '%s' is invalid (e.g., contains letters, decimals, is negative, or zero)."
                    .formatted(invalidIds));
        }
    }

    public Resource getSongById(final Integer requestId) {
        log.debug("Fetching song by ID: '{}'", requestId);

        validateRequestId(requestId);

        FindByIdDto fetchedSong = queryHandler.findById(new FindByIdQuery(requestId));
        log.info("Successfully fetched song by ID' '{}'", requestId);

        return new ByteArrayResource(fetchedSong.getData());
    }

    private void validateRequestId(Integer requestId) {
        if (Objects.isNull(requestId) || requestId <= 0) {
            log.error("The provided ID '%s' is invalid (e.g., contains letters, decimals, is negative, or zero).", requestId);
            throw new GetSongByIdException("The provided ID: '%s' is invalid (e.g., contains letters, decimals, is negative, or zero)."
                    .formatted(requestId));
        }
    }

    public UploadedSongResponseDto saveSongAndMetadata(final byte[] audioFile) {
        log.debug("Starting saving song and metadata.");

        if (audioFile == null || audioFile.length == 0) {
            return new UploadedSongResponseDto();
        }

        Metadata audioMetadata = CompletableFuture.supplyAsync(() -> AudioParserUtil.parseMp3File(audioFile))
                .join();

        SongMetadataRequestDto songMetadataRequestDto = CompletableFuture.supplyAsync(
                        () -> metadataExtractor.extractData(audioMetadata))
                .join();

        Song rawSong = Song.builder().data(audioFile).build();

        validateSongIsExist(rawSong, songMetadataRequestDto);
//        RequestDtoToCommandDtoConverter converter =
        Song savedSong = resourceRepository.save(rawSong);
        songMetadataRequestDto.setId(savedSong.getId());

        saveMetadata(songMetadataRequestDto, savedSong.getId());

        return new UploadedSongResponseDto(savedSong.getId());
    }

    private void saveMetadata(SongMetadataRequestDto songMetadataRequestDto, Integer songId) {
        try {
            log.debug("Saving metadata for ID: '{}'", songId);
            songServiceFeignClient.saveMetadata(songMetadataRequestDto);
            log.info("Successfully saved metadata for ID: '{}'", songId);
        } catch (FeignException ex) {
            log.error("Failed to save metadata for song ID '{}': {}", songId, ex.contentUTF8());
            throw new ResourceDeletionException("Failed to save metadata with resource ID: '%s'.".formatted(songId));
        }
    }

    private void validateSongIsExist(Song rawSong, SongMetadataRequestDto songMetadataRequestDto) {
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("data", ExampleMatcher.GenericPropertyMatcher::exact);
        Example<Song> songExample = Example.of(rawSong, matcher);

        boolean isExists = resourceRepository.exists(songExample);

        if (isExists) {
            String songInfo = String.format("Name: %s, Artist: %s",
                    songMetadataRequestDto.getName(), songMetadataRequestDto.getArtist());
            log.error("Duplicate song detected: '{}'", songInfo);
            throw new SongAlreadyExistException("Song already exist with info: '%s'.".formatted(songInfo));
        }
    }
}
