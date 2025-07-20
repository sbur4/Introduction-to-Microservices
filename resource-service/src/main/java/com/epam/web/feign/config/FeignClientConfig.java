package com.epam.web.feign.config;

import com.epam.web.feign.fallback.SongServiceFeignClientFallback;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import feign.Feign;
import feign.Logger;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.feign.FeignDecorators;
import io.github.resilience4j.feign.Resilience4jFeign;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class FeignClientConfig {

    private final CircuitBreakerRegistry circuitBreakerRegistry;
    private final RateLimiterRegistry rateLimiterRegistry;
    private final RetryRegistry retryRegistry;

    private static final String BACKEND_NAME = "song-service";

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }

    @Bean
    public Logger.Level feignLoggerLevel() {
        log.info("Setting Feign Logger Level to FULL for backend: {}", BACKEND_NAME);
        return Logger.Level.FULL;
    }

    @Bean
    public Encoder feignEncoder() {
        log.info("Creating Feign Encoder (SpringFormEncoder with SpringEncoder).");
        return new JacksonEncoder(objectMapper());
    }

    @Bean
    public Decoder feignDecoder() {
        log.info("Creating Feign Decoder (SpringDecoder).");
        return new JacksonDecoder(objectMapper());
    }

    @Bean
    public Feign.Builder feignBuilder(Decoder decoder, Encoder encoder) {
        log.debug("Configuring Feign Builder for backend: {}", BACKEND_NAME);

        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(BACKEND_NAME);
        RateLimiter rateLimiter = rateLimiterRegistry.rateLimiter(BACKEND_NAME);
        Retry retry = retryRegistry.retry(BACKEND_NAME);

        log.debug("Resilience4j components for {}: CircuitBreaker={}, RateLimiter={}, Retry={}",
                BACKEND_NAME, circuitBreaker.getName(), rateLimiter.getName(), retry.getName());

        final FeignDecorators decorators = FeignDecorators.builder()
                .withFallbackFactory(exception -> new SongServiceFeignClientFallback(exception, objectMapper()))
                .withCircuitBreaker(circuitBreaker)
                .withRetry(retry)
                .withRateLimiter(rateLimiter)
                .build();

        return Feign.builder()
                .encoder(encoder)
                .decoder(decoder)
                .logger(new Logger.ErrorLogger())
                .logLevel(feignLoggerLevel())
                .addCapability(Resilience4jFeign.capability(decorators));
    }
}
