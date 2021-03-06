package com.kociszewski.moviekeeper.domain.events;

import lombok.*;

@Value
public class CastCreatedEvent {
    String castId;
    String movieId;
    String externalMovieId;
}
