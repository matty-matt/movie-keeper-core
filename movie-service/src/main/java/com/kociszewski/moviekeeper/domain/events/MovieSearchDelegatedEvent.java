package com.kociszewski.moviekeeper.domain.events;

import lombok.*;

@Value
public class MovieSearchDelegatedEvent {
    private String movieId;
    private String searchPhrase;
}
