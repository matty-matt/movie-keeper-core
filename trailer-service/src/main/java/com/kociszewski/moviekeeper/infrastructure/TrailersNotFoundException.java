package com.kociszewski.moviekeeper.infrastructure;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class TrailersNotFoundException extends RuntimeException {
    public TrailersNotFoundException() {
        super();
    }
}
