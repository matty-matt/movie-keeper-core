package com.kociszewski.castservice.domain.events;

import com.kociszewski.movieservice.shared.CastEntityId;
import lombok.Value;

@Value
public class CastDeletedEvent {
    private CastEntityId castEntityId;
}
