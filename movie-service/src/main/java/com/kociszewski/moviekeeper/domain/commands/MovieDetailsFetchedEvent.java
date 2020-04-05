package com.kociszewski.moviekeeper.domain.commands;


import com.kociszewski.moviekeeper.shared.ExternalMovie;
import lombok.Value;

@Value
public class MovieDetailsFetchedEvent {
    private String proxyId;
    private ExternalMovie externalMovie;
}
