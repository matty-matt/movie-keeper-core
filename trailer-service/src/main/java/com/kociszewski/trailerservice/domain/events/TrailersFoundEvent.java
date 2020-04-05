package com.kociszewski.trailerservice.domain.events;

import lombok.Value;

@Value
public class TrailersFoundEvent {
    private String movieId;
    private String trailerEntityId;
    private String externalMovieId;
}
