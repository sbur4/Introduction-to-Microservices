package com.epam.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cloud.loadbalancer.cache.LoadBalancerCacheProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Slf4j
@EnableCaching
@Configuration
public class CaffeineCacheConfig {

    @Bean
    @Primary
    public CacheManager initCacheManager(LoadBalancerCacheProperties properties) {
        log.info("Initializing Caffeine Cache Manager for LoadBalancer");
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(
                Caffeine.newBuilder()
                        .initialCapacity(properties.getCapacity())
                        .maximumSize(properties.getCapacity() * 2L)
                        .expireAfterWrite(properties.getTtl())
                        .recordStats()
                        .removalListener((key, value, cause) ->
                                log.trace("Cache entry removed. Key: '{}', Cause: {}", key, cause)
                        )
        );

        log.debug("Cache manager initialized.");
        return cacheManager;
    }
}
