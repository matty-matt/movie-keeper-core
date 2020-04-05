package com.kociszewski.movieservice.domain.commands;

import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Value
public class FindMovieCommand {
    @TargetAggregateIdentifier
    private String movieId;
    private String phrase;
}
