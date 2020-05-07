package com.kociszewski.moviekeeper.domain.commands;

import lombok.*;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Value
public class FetchMovieDetailsCommand {
    @TargetAggregateIdentifier
    private String proxyId;
    private String searchPhrase;
}
