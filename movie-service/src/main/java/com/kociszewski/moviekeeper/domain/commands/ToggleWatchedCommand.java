package com.kociszewski.moviekeeper.domain.commands;

import com.kociszewski.moviekeeper.infrastructure.Watched;
import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Value
public class ToggleWatchedCommand {
    @TargetAggregateIdentifier
    private String movieId;
    private Watched watched;
}
