package com.epam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableDiscoveryClient
@EnableRetry
@EnableFeignClients
@EnableTransactionManagement
@SpringBootApplication
public class ResourceServiceApp {

    public static void main(String[] args) {
        SpringApplication.run(ResourceServiceApp.class, args);
    }
}
