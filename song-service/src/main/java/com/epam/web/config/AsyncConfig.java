package com.epam.web.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@EnableAsync
@Configuration
@EnableConfigurationProperties(AsyncProperties.class)
public class AsyncConfig implements WebMvcConfigurer {

    static int MIN_CORES_FOR_SCALING = 4;
    static int DEFAULT_CORE_POOL_SIZE = 8;

    @Autowired
    private AsyncProperties asyncProperties;

    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer asyncSupportConfigurer) {
        asyncSupportConfigurer.setTaskExecutor(initAsyncTaskExecutor())
                .setDefaultTimeout(asyncProperties.getAsyncRequestTimeout());
    }

    @Bean(name = "asyncTaskExecutor")
    public AsyncTaskExecutor initAsyncTaskExecutor() {
        int availableCores = calculateOptimalCorePoolSize();
        log.info("Available processors set to '{}'", availableCores);

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        configureExecutor(executor, availableCores);
        executor.initialize();

        log.info("ThreadPoolTaskExecutor initialized successfully with thread prefix '{}'.", executor.getThreadNamePrefix());
        return executor;
    }

    private int calculateOptimalCorePoolSize() {
        int availableCores = Runtime.getRuntime().availableProcessors();

        if (availableCores < asyncProperties.getMinCorePoolSize()) {
            log.warn("Insufficient cores detected: {}. Using minimum configured: {}", availableCores,
                    asyncProperties.getMinCorePoolSize());
            return asyncProperties.getMinCorePoolSize();
        }

        if (availableCores < MIN_CORES_FOR_SCALING) {
            log.info("Small core count ({}) detected. Using default core pool size: {}", availableCores,
                    DEFAULT_CORE_POOL_SIZE);
            return DEFAULT_CORE_POOL_SIZE;
        }

        return availableCores;
    }

    private void configureExecutor(ThreadPoolTaskExecutor executor, int basePoolSize) {
        executor.setCorePoolSize(basePoolSize);
        executor.setMaxPoolSize(basePoolSize * asyncProperties.getMaxPoolMultiplier());
        executor.setQueueCapacity(basePoolSize * asyncProperties.getQueueCapacityMultiplier());
        executor.setThreadNamePrefix(asyncProperties.getThreadNamePrefix());
        executor.setKeepAliveSeconds(asyncProperties.getKeepAlive());
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(asyncProperties.getShutdownWaitTime());
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        log.info("Thread pool settings: CorePoolSize='{}', MaxPoolSize='{}', QueueCapacity='{}'.",
                executor.getCorePoolSize(), executor.getMaxPoolSize(), executor.getQueueCapacity());

    }
}
