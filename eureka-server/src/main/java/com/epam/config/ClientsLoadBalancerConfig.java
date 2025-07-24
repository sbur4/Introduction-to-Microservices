package com.epam.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.List;

// [INFO]: DiscoveryClientServiceInstanceListSupplier + EurekaDiscoveryClient
@Slf4j
@Configuration
@LoadBalancerClients({
        @LoadBalancerClient(name = "resource-service"),
        @LoadBalancerClient(name = "song-service")
})
public class ClientsLoadBalancerConfig {

    @Value("${spring.cloud.loadbalancer.services}")
    private List<String> services;

    @Bean
    @LoadBalanced
    public RestTemplateBuilder initRestTemplateBuilder() {
        log.debug("Initializing LoadBalanced RestTemplate builder for services: {}", StringUtils.join(services, ", "));

        return new RestTemplateBuilder()
                .additionalInterceptors((request, body, execution) -> {
                    log.trace("Load balanced request to: {}", request.getURI());
                    return execution.execute(request, body);
                });
    }

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }
}
