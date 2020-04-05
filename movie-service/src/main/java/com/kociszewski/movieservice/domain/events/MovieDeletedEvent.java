package com.kociszewski.movieservice.domain.events;

import lombok.Value;

@Value
public class MovieDeletedEvent {
    private String movieId;
}
