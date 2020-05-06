package com.kociszewski.moviekeeper.domain.commands;

import com.kociszewski.moviekeeper.infrastructure.ExternalMovie;
import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Value
public class SaveMovieCommand {
    @TargetAggregateIdentifier
    private String movieId;
    private ExternalMovie externalMovie;
}
