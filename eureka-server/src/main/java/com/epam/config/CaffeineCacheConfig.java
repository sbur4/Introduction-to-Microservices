package com.epam.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cloud.loadbalancer.cache.LoadBalancerCacheProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@EnableCaching
@Configuration
public class CaffeineCacheConfig {

    @Value("${spring.cache.cache-names}")
    private List<String> cacheNames;

    @Bean
    @Primary
    public CacheManager initCacheManager(LoadBalancerCacheProperties loadBalancerCacheProperties) {
        log.info("Initializing Caffeine Cache Manager with capacity: {}, TTL: {}", loadBalancerCacheProperties.getCapacity(),
                loadBalancerCacheProperties.getTtl());

        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(caffeineCacheBuilder(loadBalancerCacheProperties));
        cacheManager.setCacheNames(cacheNames);

        log.debug("Cache manager initialized.");
        return cacheManager;
    }

    private Caffeine<Object, Object> caffeineCacheBuilder(LoadBalancerCacheProperties loadBalancerCacheProperties) {
        return Caffeine.newBuilder()
                .initialCapacity(loadBalancerCacheProperties.getCapacity())
                .maximumSize(calculateMaxSize(loadBalancerCacheProperties.getCapacity()))
                .expireAfterWrite(loadBalancerCacheProperties.getTtl().toMillis(), TimeUnit.MINUTES)
                .recordStats()
                .removalListener((key, value, cause) ->
                        log.trace("Cache entry removed. Key: '{}', Cause: {}",
                                StringUtils.abbreviate(String.valueOf(key), 50), cause)
                );
    }

    private long calculateMaxSize(int capacity) {
        return capacity > 0 ? capacity * 2L : 1000L;
    }
}
