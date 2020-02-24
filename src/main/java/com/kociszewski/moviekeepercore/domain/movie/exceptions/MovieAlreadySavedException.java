package com.kociszewski.moviekeepercore.domain.movie.exceptions;

import com.kociszewski.moviekeepercore.shared.model.ExternalMovieId;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MovieAlreadySavedException extends RuntimeException {
    public MovieAlreadySavedException(ExternalMovieId externalMovieId) {
        super();
        log.error("Movie already saved: {}", externalMovieId);
    }
}
