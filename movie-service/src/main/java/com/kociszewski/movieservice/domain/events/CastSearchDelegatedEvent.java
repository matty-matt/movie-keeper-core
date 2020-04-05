package com.kociszewski.movieservice.domain.events;

import lombok.Value;

@Value
public class CastSearchDelegatedEvent {
    private String movieId;
    private String externalMovieId;
}
