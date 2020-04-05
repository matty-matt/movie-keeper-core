package com.kociszewski.moviekeeper.domain.commands;

import com.kociszewski.moviekeeper.shared.ExternalMovie;
import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Value
public class SaveMovieDetailsCommand {
    @TargetAggregateIdentifier
    private String proxyId;
    private ExternalMovie externalMovie;
}
