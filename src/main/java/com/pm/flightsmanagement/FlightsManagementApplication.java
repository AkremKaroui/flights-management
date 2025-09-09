package com.pm.flightsmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class FlightsManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(FlightsManagementApplication.class, args);
    }

}
