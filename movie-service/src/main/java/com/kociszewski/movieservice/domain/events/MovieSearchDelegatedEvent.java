package com.kociszewski.movieservice.domain.events;

import com.kociszewski.movieservice.shared.SearchPhrase;
import lombok.Value;

@Value
public class MovieSearchDelegatedEvent {
    private String movieId;
    private String trailerEntityId;
    private String castEntityId;
    private SearchPhrase searchPhrase;
}
