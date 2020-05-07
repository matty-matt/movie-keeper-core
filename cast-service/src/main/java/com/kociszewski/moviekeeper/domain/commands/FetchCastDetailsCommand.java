package com.kociszewski.moviekeeper.domain.commands;

import lombok.*;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Value
public class FetchCastDetailsCommand {
    @TargetAggregateIdentifier
    private String proxyId;
    private String externalMovieId;
    private String castId;
}
