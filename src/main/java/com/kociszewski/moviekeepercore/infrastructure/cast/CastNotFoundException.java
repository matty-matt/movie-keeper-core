package com.kociszewski.moviekeepercore.infrastructure.cast;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CastNotFoundException extends RuntimeException {
    public CastNotFoundException() {
        super();
    }
}
