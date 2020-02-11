package com.kociszewski.moviekeepercore.infrastructure.access.mappedexceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
@Slf4j
public class MovieAlreadyAddedException extends RuntimeException {
    public MovieAlreadyAddedException(String message) {
        super(message);
        log.error(message);
    }
}
