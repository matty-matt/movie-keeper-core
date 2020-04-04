package com.kociszewski.castservice.domain.events;

import com.kociszewski.movieservice.infrastructure.cast.CastDTO;
import lombok.Value;

@Value
public class CastSavedEvent {
    private CastDTO castDTO;
}
