package com.kociszewski.moviekeeper.domain.commands;

import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Value
public class CreateRefreshMoviesCommand {
    @TargetAggregateIdentifier
    String refreshId;
}
