package com.kociszewski.moviekeeper.domain.events;

import lombok.Value;

@Value
public class TrailersFetchDelegatedEvent {
    private String proxyId;
    private String externalMovieId;
    private String trailersId;
}
