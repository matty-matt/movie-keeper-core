package com.kociszewski.moviekeepercore.domain.movie.events;

import com.kociszewski.moviekeepercore.shared.model.MovieId;
import com.kociszewski.moviekeepercore.shared.model.Watched;
import lombok.Value;

@Value
public class MovieWatchedEvent {
    private MovieId movieId;
    private Watched watched;
}
