package com.kociszewski.movieservice.domain.events;

import com.kociszewski.movieservice.shared.MovieId;
import com.kociszewski.movieservice.shared.ExternalMovie;
import lombok.Value;

@Value
public class MovieSavedEvent {
    private MovieId movieId;
    private ExternalMovie externalMovie;
}
