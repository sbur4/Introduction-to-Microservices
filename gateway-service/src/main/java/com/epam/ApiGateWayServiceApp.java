package com.epam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

// API gateway pattern
@EnableDiscoveryClient
@SpringBootApplication
public class ApiGateWayServiceApp {
    public static void main(String[] args) {
        SpringApplication.run(ApiGateWayServiceApp.class, args);
    }
}
