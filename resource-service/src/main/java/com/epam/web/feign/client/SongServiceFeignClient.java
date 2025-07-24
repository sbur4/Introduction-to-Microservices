package com.epam.web.feign.client;

import com.epam.core.dto.request.SongMetadataRequestDto;
import com.epam.core.dto.response.DeletedByIdsResponseDto;
import com.epam.core.dto.response.SongMetadataIdResponseDto;
import com.epam.web.feign.fallback.SongServiceFeignClientFallback;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

// [NOTE]: Circuit breaker design pattern
// [NOTE]: Retry design pattern
@CircuitBreaker(name = "${song.service.name}")
@FeignClient(name = "${song.service.name}", url = "${song.service.base-url}", fallback = SongServiceFeignClientFallback.class)
public interface SongServiceFeignClient {

    @Retry(name = "${song.service.name}")
    @PostMapping(consumes = "application/json")
    SongMetadataIdResponseDto saveMetadata(@RequestBody SongMetadataRequestDto requestDto);

    @Retry(name = "${song.service.name}")
    @DeleteMapping
    DeletedByIdsResponseDto deleteMetadataByIds(@RequestParam("id") String requestIds);
}
