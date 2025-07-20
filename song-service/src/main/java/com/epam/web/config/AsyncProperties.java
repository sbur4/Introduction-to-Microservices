package com.epam.web.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@ConfigurationProperties(prefix = "app.async")
public final class AsyncProperties {

    Integer minCorePoolSize;
    Integer maxPoolMultiplier;
    Integer queueCapacityMultiplier;
    String threadNamePrefix;
    Integer keepAlive;
    Integer asyncRequestTimeout;
    Integer shutdownWaitTime;
}
