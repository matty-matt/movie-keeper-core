package com.kociszewski.moviekeeper.external.cast;

import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Value
public class FetchCastCommand {
    @TargetAggregateIdentifier
    private String castId;
    private String externalMovieId;
}
