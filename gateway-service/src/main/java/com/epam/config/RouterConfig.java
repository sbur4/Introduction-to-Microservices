package com.epam.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouterConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("resource-service", r -> r.path("/resources")
                        .and().method("POST", "GET", "DELETE")
                        .uri("http://localhost:8071"))
                .route("song-service", r -> r.path("/songs")
                        .and().method("POST", "GET", "DELETE")
                        .uri("http://localhost:8072"))
                .build();
    }
}
