package com.kociszewski.moviekeeper.domain.events;

import com.kociszewski.moviekeeper.shared.Watched;
import lombok.Value;

@Value
public class ToggleWatchedEvent {
    private String movieId;
    private Watched watched;
}
