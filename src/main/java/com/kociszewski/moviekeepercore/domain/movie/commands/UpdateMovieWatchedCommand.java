package com.kociszewski.moviekeepercore.domain.movie.commands;

import com.kociszewski.moviekeepercore.shared.model.Watched;
import com.kociszewski.moviekeepercore.shared.model.MovieId;
import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Value
public class UpdateMovieWatchedCommand {
    @TargetAggregateIdentifier
    private MovieId movieId;
    private Watched watched;
}
