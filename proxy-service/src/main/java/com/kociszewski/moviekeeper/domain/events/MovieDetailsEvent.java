package com.kociszewski.moviekeeper.domain.events;

import com.kociszewski.moviekeeper.infrastructure.ExternalMovie;
import lombok.Value;

@Value
public class MovieDetailsEvent {
     String proxyId;
     ExternalMovie externalMovie;
}
