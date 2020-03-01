package com.kociszewski.moviekeepercore.domain.cast.events;

import com.kociszewski.moviekeepercore.shared.model.CastEntityId;
import lombok.Value;

@Value
public class CastDeletedEvent {
    private CastEntityId castEntityId;
}
