package com.kociszewski.moviekeeper.domain.commands;

import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Value
public class DeleteTrailersCommand {
    @TargetAggregateIdentifier
    String trailersId;
}
