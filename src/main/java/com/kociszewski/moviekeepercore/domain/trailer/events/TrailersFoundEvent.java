package com.kociszewski.moviekeepercore.domain.trailer.events;

import com.kociszewski.moviekeepercore.shared.model.ExternalMovieId;
import com.kociszewski.moviekeepercore.shared.model.MovieId;
import com.kociszewski.moviekeepercore.shared.model.TrailerEntityId;
import lombok.Value;

@Value
public class TrailersFoundEvent {
    private MovieId movieId;
    private TrailerEntityId trailerEntityId;
    private ExternalMovieId externalMovieId;
}
