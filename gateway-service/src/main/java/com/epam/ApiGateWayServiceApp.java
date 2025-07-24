package com.epam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableAsync;

// [NOTE]: API gateway pattern or BFF
@EnableAsync
@EnableDiscoveryClient
@SpringBootApplication
public class ApiGateWayServiceApp {
    public static void main(String[] args) {
        SpringApplication.run(ApiGateWayServiceApp.class, args);
    }
}
