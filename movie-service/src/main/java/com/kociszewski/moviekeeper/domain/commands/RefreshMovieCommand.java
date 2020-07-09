package com.kociszewski.moviekeeper.domain.commands;

import com.kociszewski.moviekeeper.infrastructure.RefreshData;
import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Value
public class RefreshMovieCommand {
    @TargetAggregateIdentifier
    String movieId;
    RefreshData refreshData;
}
