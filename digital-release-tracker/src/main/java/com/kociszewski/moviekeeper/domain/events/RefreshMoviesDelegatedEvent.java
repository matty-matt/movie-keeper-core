package com.kociszewski.moviekeeper.domain.events;

import com.kociszewski.moviekeeper.infrastructure.RefreshMovie;
import lombok.Value;

import java.util.List;

@Value
public class RefreshMoviesDelegatedEvent {
    String refreshId;
    List<RefreshMovie> moviesToRefresh;
}
