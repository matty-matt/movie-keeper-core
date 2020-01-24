package com.kociszewski.moviekeepercore.domain.movie.events;

import com.kociszewski.moviekeepercore.domain.movie.info.MovieId;
import com.kociszewski.moviekeepercore.shared.model.ExternalMovie;
import lombok.Value;

@Value
public class MovieSavedEvent {
    private MovieId movieId;
    private ExternalMovie externalMovie;
}
