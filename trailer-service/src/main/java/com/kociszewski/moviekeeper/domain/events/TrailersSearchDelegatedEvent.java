package com.kociszewski.moviekeeper.domain.events;

import lombok.Value;

@Value
public class TrailersSearchDelegatedEvent {
     String trailersId;
     String movieId;
     String externalMovieId;
}
