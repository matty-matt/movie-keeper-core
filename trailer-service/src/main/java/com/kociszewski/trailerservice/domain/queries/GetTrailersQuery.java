package com.kociszewski.trailerservice.domain.queries;

import lombok.Value;

@Value
public class GetTrailersQuery {
    private String movieId;
}
