package com.kociszewski.moviekeeper.domain.events;

import com.kociszewski.moviekeeper.infrastructure.ExternalMovie;
import lombok.Value;

@Value
public class MovieSavedEvent {
    private String movieId;
    private ExternalMovie externalMovie;
}