package com.kociszewski.moviekeeper.domain.events;

import lombok.*;

@Value
public class CastFoundEvent {
    String castId;
    String externalMovieId;
}
