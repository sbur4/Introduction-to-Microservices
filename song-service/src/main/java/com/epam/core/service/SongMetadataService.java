package com.epam.core.service;

import com.epam.core.cqrs.command.DeleteByIdsCommand;
import com.epam.core.cqrs.command.SaveEntityCommand;
import com.epam.core.cqrs.handler.CommandHandler;
import com.epam.core.cqrs.handler.QueryHandler;
import com.epam.core.cqrs.query.FindByIdQuery;
import com.epam.core.cqrs.query.FindByIdsQuery;
import com.epam.core.dto.request.SongMetadataRequestDto;
import com.epam.core.dto.response.DeletedByIdsResponseDto;
import com.epam.core.dto.response.SongMetadataIdResponseDto;
import com.epam.core.dto.response.SongMetadataResponseDto;
import com.epam.core.exception.DeleteMetadataByIdsException;
import com.epam.core.exception.GetMetadataByIdException;
import com.epam.core.exception.MetadataAlreadyExistException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class SongMetadataService {

    private final CommandHandler commandHandler;
    private final QueryHandler queryHandler;
    private final ConversionService conversionService;

    public DeletedByIdsResponseDto deleteMetadataByIds(final String requestIds) {
        log.debug("Starting delete metadata by ID's: '{}'", requestIds);

        validateRequestIds(requestIds);

        List<Integer> splittedIds = splitIds(requestIds);
        log.debug("Validated and parsed ID's: '{}'", splittedIds);

        DeletedByIdsResponseDto responseDto = queryHandler.findByIds(new FindByIdsQuery(splittedIds));
        List<Integer> idsForRemoving = responseDto.ids();

        if (CollectionUtils.isNotEmpty(idsForRemoving)) {
            log.debug("Deleting metadata by ID's': '{}'", idsForRemoving);
            commandHandler.deleteByIds(new DeleteByIdsCommand(idsForRemoving));
        }

        return responseDto;
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
            throw new DeleteMetadataByIdsException("CSV string cannot be empty.");
        }

        if (requestIds.length() > 200) {
            log.error("Restriction: CSV string length must be less than 200 characters. Actual length: '{}'.", requestIds.length());
            throw new DeleteMetadataByIdsException("Actual length: '{%d}'. %s".formatted(requestIds.length(),
                    "CSV string length must be less than 200 characters."));
        }

        List<String> invalidIds = Arrays.stream(StringUtils.split(requestIds, ","))
                .map(StringUtils::strip)
                .filter(i -> !StringUtils.isNumeric(i))
                .toList();

        if (CollectionUtils.isNotEmpty(invalidIds)) {
            log.error("Restriction: The provided ID's: '{}' is invalid (e.g., contains letters, decimals, is negative, or zero).", invalidIds);
            throw new DeleteMetadataByIdsException("The provided ID's: '%s' is invalid (e.g., contains letters, decimals, is negative, or zero)."
                    .formatted(invalidIds));
        }
    }

    public SongMetadataResponseDto getMetadataById(final Integer requestId) {
        log.debug("Fetching song metadata for ID: '{}'", requestId);

        validateRequestId(requestId);

        SongMetadataResponseDto responseDto = queryHandler.findById(new FindByIdQuery(requestId));

        log.info("Successfully fetched song metadata for ID: '{}'", requestId);
        return responseDto;
    }

    private void validateRequestId(Integer requestId) {
        if (Objects.isNull(requestId) || requestId <= 0) {
            log.error("The provided ID: '{}' is invalid (e.g., contains letters, decimals, is negative, or zero).", requestId);
            throw new GetMetadataByIdException("The provided ID: '%s' is invalid (e.g., contains letters, decimals, is negative, or zero)."
                    .formatted(requestId));
        }
    }

    public SongMetadataIdResponseDto saveMetadata(SongMetadataRequestDto requestDto) {
        log.debug("Starting saving metadata with ID: '{}'", requestDto.getId());

        validateRequestDto(requestDto.getId());

        SaveEntityCommand saveCommand = conversionService.convert(requestDto, SaveEntityCommand.class);
        return commandHandler.saveMetadata(saveCommand);
    }

    private void validateRequestDto(Integer requestId) {
        boolean isMetadataExist = queryHandler.isExistByResourceId(new FindByIdQuery(requestId));

        if (isMetadataExist) {
            log.error("Metadata existence check for ID '{}': '{}'", requestId, isMetadataExist);
            throw new MetadataAlreadyExistException("Metadata already exists for the given ID: '%d'".formatted(requestId));
        }
    }
}
