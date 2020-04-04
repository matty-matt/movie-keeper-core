package com.kociszewski.movieservice.domain.events;

import com.kociszewski.movieservice.shared.MovieId;
import com.kociszewski.movieservice.shared.Watched;
import lombok.Value;

@Value
public class ToggleWatchedEvent {
    private MovieId movieId;
    private Watched watched;
}
