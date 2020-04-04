package com.kociszewski.movieservice.domain.commands;

import com.kociszewski.movieservice.shared.Watched;
import com.kociszewski.movieservice.shared.MovieId;
import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Value
public class ToggleWatchedCommand {
    @TargetAggregateIdentifier
    private MovieId movieId;
    private Watched watched;
}
