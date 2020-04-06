package com.kociszewski.moviekeeper.domain.events;

import lombok.Value;

@Value
public class CastDeletedEvent {
    private String castEntityId;
}
