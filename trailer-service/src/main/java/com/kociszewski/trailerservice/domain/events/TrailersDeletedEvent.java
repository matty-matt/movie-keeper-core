package com.kociszewski.trailerservice.domain.events;

import com.kociszewski.movieservice.shared.TrailerEntityId;
import lombok.Value;

@Value
public class TrailersDeletedEvent {
    private TrailerEntityId trailerEntityId;
}
