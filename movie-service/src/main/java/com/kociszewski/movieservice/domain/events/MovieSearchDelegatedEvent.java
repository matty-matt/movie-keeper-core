package com.kociszewski.movieservice.domain.events;

import lombok.Value;

@Value
public class MovieSearchDelegatedEvent {
    private String movieId;
    private String searchPhrase;
}
