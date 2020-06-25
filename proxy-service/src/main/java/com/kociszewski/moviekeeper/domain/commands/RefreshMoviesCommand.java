package com.kociszewski.moviekeeper.domain.commands;

import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.List;

@Value
public class RefreshMoviesCommand {
    @TargetAggregateIdentifier
    String proxyId;
    List<String> moviesToRefresh;
}
