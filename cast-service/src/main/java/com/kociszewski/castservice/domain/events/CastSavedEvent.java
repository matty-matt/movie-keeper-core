package com.kociszewski.castservice.domain.events;

import com.kociszewski.castservice.infrastructure.CastDTO;
import lombok.Value;

@Value
public class CastSavedEvent {
    private CastDTO castDTO;
}
