package com.kociszewski.moviekeeper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DigitalReleaseTrackerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DigitalReleaseTrackerApplication.class, args);
    }
}
