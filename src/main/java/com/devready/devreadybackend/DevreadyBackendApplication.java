package com.devready.devreadybackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling // enables the @Scheduled annotation in SchedulerService
public class DevreadyBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(DevreadyBackendApplication.class, args);
    }
}