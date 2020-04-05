package com.kociszewski.movieservice.domain.commands;

import com.kociszewski.movieservice.shared.ExternalMovie;
import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Value
public class SaveMovieCommand {
    @TargetAggregateIdentifier
    private String movieId;
    private ExternalMovie externalMovie;
}
