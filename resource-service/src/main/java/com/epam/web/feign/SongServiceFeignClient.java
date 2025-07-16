package com.epam.web.feign;

import com.epam.core.dto.request.SongMetadataRequestDto;
import com.epam.core.dto.response.DeletedByIdsResponseDto;
import com.epam.core.dto.response.SongMetadataIdResponseDto;
import com.epam.core.dto.response.SongMetadataResponseDto;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.ResourceAccessException;

@CircuitBreaker(name = "${song.service.name}")
@FeignClient(name = "${song.service.name}", url = "${song.service.base-url}")
public interface SongServiceFeignClient {

    @Retryable(
            retryFor = {ResourceAccessException.class, FeignException.ServiceUnavailable.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000, multiplier = 2))
    @PostMapping(consumes = "application/json")
    SongMetadataIdResponseDto saveMetadata(@RequestBody SongMetadataRequestDto requestDto);

    @Retryable(
            retryFor = {ResourceAccessException.class, FeignException.ServiceUnavailable.class},
            maxAttempts = 2,
            backoff = @Backoff(delay = 2000))
    @GetMapping(path = "/{id}")
    SongMetadataResponseDto getMetadataById(@PathVariable("id") Integer requestId);

    @Retryable(
            retryFor = {ResourceAccessException.class, FeignException.ServiceUnavailable.class},
            maxAttempts = 2,
            backoff = @Backoff(delay = 2000))
    @DeleteMapping
    DeletedByIdsResponseDto deleteMetadataByIds(@RequestParam("id") String requestIds);
}
