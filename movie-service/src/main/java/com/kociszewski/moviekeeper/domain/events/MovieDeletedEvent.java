package com.kociszewski.moviekeeper.domain.events;

import lombok.Value;

@Value
public class MovieDeletedEvent {
     String movieId;
     String trailersId;
     String castId;
}
