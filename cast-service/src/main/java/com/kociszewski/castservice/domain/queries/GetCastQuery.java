package com.kociszewski.castservice.domain.queries;

import com.kociszewski.movieservice.shared.MovieId;
import lombok.Value;

@Value
public class GetCastQuery {
    private MovieId movieId;
}
