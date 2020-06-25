package com.kociszewski.moviekeeper.domain.events;

import lombok.Value;

import java.util.List;

@Value
public class RefreshMoviesDelegatedEvent {
    String refreshId;
    List<String> moviesToRefresh;
}
