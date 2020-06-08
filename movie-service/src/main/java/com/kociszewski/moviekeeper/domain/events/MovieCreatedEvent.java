package com.kociszewski.moviekeeper.domain.events;

import lombok.*;

@Value
public class MovieCreatedEvent {
     String movieId;
     String searchPhrase;
}
