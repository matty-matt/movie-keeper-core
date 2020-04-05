package com.kociszewski.moviekeeper.domain.events;

import lombok.Value;

@Value
public class MovieFetchDelegatedEvent {
    private String proxyId;
    private String searchPhrase;
}
