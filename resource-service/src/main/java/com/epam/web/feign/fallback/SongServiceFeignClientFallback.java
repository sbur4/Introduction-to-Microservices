package com.epam.web.feign.fallback;

import com.epam.core.dto.request.SongMetadataRequestDto;
import com.epam.core.dto.response.DeletedByIdsResponseDto;
import com.epam.core.dto.response.SongMetadataIdResponseDto;
import com.epam.web.feign.client.SongServiceFeignClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SongServiceFeignClientFallback extends MicroserviceFallback implements SongServiceFeignClient {

    public SongServiceFeignClientFallback(Exception exception, ObjectMapper objectMapper) {
        super(exception, objectMapper);
    }

    @Override
    public SongMetadataIdResponseDto saveMetadata(SongMetadataRequestDto requestDto) {
        log.error("Error during attempt to save metadata with ID '{}';", requestDto.getId());
        throw throwException("Error during attempt to save metadata with ID '%d'".formatted(requestDto.getId()));
    }

    @Override
    public DeletedByIdsResponseDto deleteMetadataByIds(String requestIds) {
        log.error("Error during attempt to delete metadata by ID's '{}';", requestIds);
        throw throwException("Error during attempt to delete metadata by ID's '%s'".formatted(requestIds));
    }
}
