package com.kociszewski.moviekeepercore.domain.cast.events;

import com.kociszewski.moviekeepercore.infrastructure.cast.CastDTO;
import lombok.Value;

@Value
public class CastSavedEvent {
    private CastDTO castDTO;
}
