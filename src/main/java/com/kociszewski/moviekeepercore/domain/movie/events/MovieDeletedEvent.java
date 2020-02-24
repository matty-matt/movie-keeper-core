package com.kociszewski.moviekeepercore.domain.movie.events;

import com.kociszewski.moviekeepercore.shared.model.MovieId;
import lombok.Value;

@Value
public class MovieDeletedEvent {
    private MovieId movieId;
}
