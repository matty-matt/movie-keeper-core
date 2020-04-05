package com.kociszewski.castservice.domain.commands;

import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Value
public class FindCastCommand {
    @TargetAggregateIdentifier
    private String movieId;
    private String externalMovieId;
}
