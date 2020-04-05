package com.kociszewski.movieservice.domain.commands;

import com.kociszewski.movieservice.shared.Watched;
import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Value
public class ToggleWatchedCommand {
    @TargetAggregateIdentifier
    private String movieId;
    private Watched watched;
}
