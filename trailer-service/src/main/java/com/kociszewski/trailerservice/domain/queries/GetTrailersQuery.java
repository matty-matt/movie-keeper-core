package com.kociszewski.trailerservice.domain.queries;

import com.kociszewski.movieservice.shared.MovieId;
import lombok.Value;

@Value
public class GetTrailersQuery {
    private MovieId movieId;
}
