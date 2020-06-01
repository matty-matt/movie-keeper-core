package com.kociszewski.moviekeeper.domain.events;

import lombok.Value;

@Value
public class TrailersAndCastSearchDelegatedEvent {
    String movieId;
    String externalMovieId;
    String trailersId;
    String castId;
}
