package com.kociszewski.moviekeeper.domain.events;

import lombok.*;

@Value
public class MovieSearchDelegatedEvent {
     String movieId;
     String searchPhrase;
}
