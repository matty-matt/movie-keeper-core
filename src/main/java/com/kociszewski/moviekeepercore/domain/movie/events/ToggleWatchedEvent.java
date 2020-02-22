package com.kociszewski.moviekeepercore.domain.movie.events;

import com.kociszewski.moviekeepercore.shared.model.MovieId;
import com.kociszewski.moviekeepercore.shared.model.Watched;
import lombok.Value;

@Value
public class ToggleWatchedEvent {
    private MovieId movieId;
    private Watched watched;
}
