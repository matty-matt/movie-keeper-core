package com.kociszewski.moviekeepercore.domain.trailer.events;

import com.kociszewski.moviekeepercore.shared.model.MovieId;
import lombok.Value;

@Value
public class TrailersDeletedEvent {
    private MovieId movieId;
}
