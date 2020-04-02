package com.kociszewski.moviekeepercore.domain.cast.events;

import com.kociszewski.moviekeepercore.shared.model.CastEntityId;
import com.kociszewski.moviekeepercore.shared.model.ExternalMovieId;
import com.kociszewski.moviekeepercore.shared.model.MovieId;
import lombok.Value;

@Value
public class CastFoundEvent {
    private MovieId movieId;
    private CastEntityId castEntityId;
    private ExternalMovieId externalMovieId;
}
