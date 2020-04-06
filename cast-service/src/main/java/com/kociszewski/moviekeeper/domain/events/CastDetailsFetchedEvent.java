package com.kociszewski.moviekeeper.domain.events;

import com.kociszewski.moviekeeper.infrastructure.CastDTO;
import lombok.Value;

@Value
public class CastDetailsFetchedEvent {
    private String proxyId;
    private CastDTO castDTO;
}
