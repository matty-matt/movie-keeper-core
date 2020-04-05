package com.kociszewski.moviekeeper.domain.events;

import lombok.Value;

@Value
public class MovieDeletedEvent {
    private String movieId;
}
