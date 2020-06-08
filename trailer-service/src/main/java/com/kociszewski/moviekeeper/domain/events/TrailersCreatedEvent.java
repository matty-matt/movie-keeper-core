package com.kociszewski.moviekeeper.domain.events;

import lombok.Value;

@Value
public class TrailersCreatedEvent {
     String trailersId;
     String movieId;
     String externalMovieId;
}
