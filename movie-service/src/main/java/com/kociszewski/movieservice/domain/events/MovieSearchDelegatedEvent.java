package com.kociszewski.movieservice.domain.events;

import com.kociszewski.movieservice.shared.CastEntityId;
import com.kociszewski.movieservice.shared.TrailerEntityId;
import com.kociszewski.movieservice.shared.MovieId;
import com.kociszewski.movieservice.shared.SearchPhrase;
import lombok.Value;

@Value
public class MovieSearchDelegatedEvent {
    private MovieId movieId;
    private TrailerEntityId trailerEntityId;
    private CastEntityId castEntityId;
    private SearchPhrase searchPhrase;
}
