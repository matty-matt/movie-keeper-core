package com.kociszewski.moviekeeper.domain.events;

import lombok.Value;

@Value
public class CastFetchDelegatedEvent {
    private String proxyId;
    private String externalMovieId;
    private String castId;
}
