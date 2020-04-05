package com.kociszewski.proxyservice.domain.events;

import com.kociszewski.proxyservice.shared.ExternalMovie;
import lombok.Value;

@Value
public class MovieDetailsFetchedEvent {
    private String proxyId;
    private ExternalMovie externalMovie;
}
