package com.kociszewski.movieservice.domain.queries;

import lombok.Value;

@Value
public class GetMovieQuery {
    private String movieId;
}
