package com.kociszewski.movieservice.domain.commands;

import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Value
public class DeleteMovieCommand {
    @TargetAggregateIdentifier
    private String movieId;
}
