package com.kociszewski.moviekeepercore.domain.movie.commands;

import com.kociszewski.moviekeepercore.shared.model.MovieId;
import com.kociszewski.moviekeepercore.shared.model.ExternalMovie;
import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Value
public class SaveMovieCommand {
    @TargetAggregateIdentifier
    private MovieId movieId;
    private ExternalMovie externalMovie;
}
