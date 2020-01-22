package com.kociszewski.moviekeepercore.domain.movie.events;

import com.kociszewski.moviekeepercore.domain.movie.info.MovieId;
import com.kociszewski.moviekeepercore.shared.model.ExternalMovieId;
import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.List;

@Value
public class MovieIdFoundEvent {
    private MovieId movieId;
    private ExternalMovieId externalMovieId;
}
