package com.kociszewski.movieservice.domain.events;

import com.kociszewski.movieservice.shared.MovieId;
import lombok.Value;

@Value
public class MovieDeletedEvent {
    private MovieId movieId;
}
