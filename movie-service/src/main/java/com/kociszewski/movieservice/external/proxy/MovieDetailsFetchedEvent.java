package com.kociszewski.movieservice.external.proxy;


import com.kociszewski.movieservice.shared.ExternalMovie;
import lombok.Value;

@Value
public class MovieDetailsFetchedEvent {
    private String proxyId;
    private ExternalMovie externalMovie;
}
