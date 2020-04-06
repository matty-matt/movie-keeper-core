package com.kociszewski.moviekeeper.domain.events;

import lombok.Value;

@Value
public class TrailersDeletedEvent {
    private String trailerEntityId;
}
