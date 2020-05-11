package com.kociszewski.moviekeeper.domain.events;

import com.kociszewski.moviekeeper.infrastructure.Watched;
import lombok.Value;

@Value
public class ToggleWatchedEvent {
     String movieId;
     Watched watched;
}
