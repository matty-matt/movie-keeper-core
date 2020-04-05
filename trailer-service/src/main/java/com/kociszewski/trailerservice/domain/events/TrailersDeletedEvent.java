package com.kociszewski.trailerservice.domain.events;

import lombok.Value;

@Value
public class TrailersDeletedEvent {
    private String trailerEntityId;
}
