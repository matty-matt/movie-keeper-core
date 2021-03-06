package com.kociszewski.moviekeeper.domain.commands;

import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Value
public class DelegateTrailersAndCastSearchCommand {
    @TargetAggregateIdentifier
    String movieId;
    String castId;
    String trailersId;
}
