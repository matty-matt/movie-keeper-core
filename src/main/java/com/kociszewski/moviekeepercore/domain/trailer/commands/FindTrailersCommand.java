package com.kociszewski.moviekeepercore.domain.trailer.commands;

import com.kociszewski.moviekeepercore.shared.model.ExternalMovieId;
import com.kociszewski.moviekeepercore.shared.model.MovieId;
import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Value
public class FindTrailersCommand {
    @TargetAggregateIdentifier
    private MovieId movieId;
    private ExternalMovieId externalMovieId;
}
