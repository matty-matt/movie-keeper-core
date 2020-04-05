package com.kociszewski.movieservice.domain.events;

import com.kociszewski.movieservice.shared.Watched;
import lombok.Value;

@Value
public class ToggleWatchedEvent {
    private String movieId;
    private Watched watched;
}
