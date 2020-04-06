package com.kociszewski.moviekeeper.domain.events;

import lombok.*;

@Value
public class CastSearchDelegatedEvent {
    String castId;
    String movieId;
    String externalMovieId;
}
