package com.kociszewski.proxyservice.domain.commands;

import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Value
public class FetchMovieDetailsCommand {
    @TargetAggregateIdentifier
    private String proxyId;
    private String searchPhrase;
}
