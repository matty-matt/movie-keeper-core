package com.kociszewski.moviekeepercore.domain.movie.queries;

import com.kociszewski.moviekeepercore.domain.movie.info.MovieId;
import com.kociszewski.moviekeepercore.shared.model.ExternalMovieId;
import lombok.Value;

@Value
public class FindMovieQuery {
    private final MovieId movieId;
}
