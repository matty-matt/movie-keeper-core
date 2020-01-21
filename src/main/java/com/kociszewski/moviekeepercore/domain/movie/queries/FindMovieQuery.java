package com.kociszewski.moviekeepercore.domain.movie.queries;

import com.kociszewski.moviekeepercore.domain.movie.info.MovieId;
import lombok.Value;

@Value
public class FindMovieQuery {
    private final MovieId movieId;
}
