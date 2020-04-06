package com.kociszewski.moviekeeper.domain.events;

import com.kociszewski.moviekeeper.infrastructure.ExternalMovie;
import lombok.Value;

@Value
public class MovieDetailsFetchedEvent {
    private String proxyId;
    private ExternalMovie externalMovie;
}
