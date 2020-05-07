package com.kociszewski.moviekeeper.domain.events;

import lombok.*;

@Value
public class CastSearchDelegatedEvent {
    private String castId;
    private String movieId;
    private String externalMovieId;
}
