package com.epam.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// INFO: DiscoveryClientServiceInstanceListSupplier + EurekaDiscoveryClient
@Slf4j
@Configuration
@LoadBalancerClients({
        @LoadBalancerClient(name = "resource-service"),
        @LoadBalancerClient(name = "song-service")
})
public class ClientsLoadBalancerConfig {

    @Bean
    @LoadBalanced
    public RestTemplateBuilder initRestTemplateBuilder() {
        log.debug("Initializing LoadBalanced RestTemplate builder...");
        return new RestTemplateBuilder();
    }
}
