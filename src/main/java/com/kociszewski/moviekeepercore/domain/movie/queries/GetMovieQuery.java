package com.kociszewski.moviekeepercore.domain.movie.queries;

import com.kociszewski.moviekeepercore.shared.model.MovieId;
import lombok.Value;

@Value
public class GetMovieQuery {
    private MovieId movieId;
}