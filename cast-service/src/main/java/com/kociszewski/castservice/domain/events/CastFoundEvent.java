package com.kociszewski.castservice.domain.events;

import com.kociszewski.movieservice.shared.CastEntityId;
import com.kociszewski.movieservice.shared.ExternalMovieId;
import com.kociszewski.movieservice.shared.MovieId;
import lombok.Value;

@Value
public class CastFoundEvent {
    private MovieId movieId;
    private CastEntityId castEntityId;
    private ExternalMovieId externalMovieId;
}
