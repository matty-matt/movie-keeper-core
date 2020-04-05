package com.kociszewski.castservice.domain.events;

import lombok.Value;

@Value
public class CastDeletedEvent {
    private String castEntityId;
}
