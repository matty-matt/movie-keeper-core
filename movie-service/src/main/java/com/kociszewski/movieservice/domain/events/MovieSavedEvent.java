package com.kociszewski.movieservice.domain.events;

import com.kociszewski.movieservice.shared.ExternalMovie;
import lombok.Value;

@Value
public class MovieSavedEvent {
    private String movieId;
    private ExternalMovie externalMovie;
}
