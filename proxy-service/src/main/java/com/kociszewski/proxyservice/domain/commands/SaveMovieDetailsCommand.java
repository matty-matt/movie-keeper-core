package com.kociszewski.proxyservice.domain.commands;

import com.kociszewski.proxyservice.shared.ExternalMovie;
import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Value
public class SaveMovieDetailsCommand {
    @TargetAggregateIdentifier
    private String proxyId;
    private ExternalMovie externalMovie;
}
