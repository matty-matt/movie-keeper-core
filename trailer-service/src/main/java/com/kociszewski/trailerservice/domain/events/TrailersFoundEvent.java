package com.kociszewski.trailerservice.domain.events;

import com.kociszewski.movieservice.shared.ExternalMovieId;
import com.kociszewski.movieservice.shared.MovieId;
import com.kociszewski.movieservice.shared.TrailerEntityId;
import lombok.Value;

@Value
public class TrailersFoundEvent {
    private MovieId movieId;
    private TrailerEntityId trailerEntityId;
    private ExternalMovieId externalMovieId;
}
