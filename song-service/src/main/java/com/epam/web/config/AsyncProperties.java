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

    int minCorePoolSize;
    int maxPoolMultiplier;
    int queueCapacityMultiplier;
    String threadNamePrefix;
    int keepAlive;
    int asyncRequestTimeout;
    int shutdownWaitTime;
}
