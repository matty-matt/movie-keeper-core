package com.kociszewski.moviekeeper.domain.events;

import lombok.Value;

@Value
public class TrailersSearchDelegatedEvent {
    private String movieId;
    private String externalMovieId;
}
