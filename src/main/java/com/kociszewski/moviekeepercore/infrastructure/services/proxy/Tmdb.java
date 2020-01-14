package com.kociszewski.moviekeepercore.infrastructure.services.proxy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Tmdb {

    public Tmdb(@Value("${api.key}") String apiKey) {
        System.out.println(apiKey);
    }
}
