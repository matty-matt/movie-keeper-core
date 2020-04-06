package com.kociszewski.moviekeeper.domain.events;

import com.kociszewski.moviekeeper.infrastructure.CastDTO;
import lombok.Value;

@Value
public class CastSavedEvent {
    private CastDTO castDTO;
}
