package com.kociszewski.movieservice.domain.queries;

import com.kociszewski.movieservice.shared.MovieId;
import lombok.Value;

@Value
public class GetMovieQuery {
    private MovieId movieId;
}
