package com.kociszewski.castservice.domain.events;

import lombok.Value;

@Value
public class CastFoundEvent {
    private String movieId;
    private String castEntityId;
    private String externalMovieId;
}
