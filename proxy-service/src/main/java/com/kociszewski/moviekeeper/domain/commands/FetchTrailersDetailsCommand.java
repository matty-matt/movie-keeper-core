package com.kociszewski.moviekeeper.domain.commands;

import lombok.*;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Value
public class FetchTrailersDetailsCommand {
    @TargetAggregateIdentifier
     String proxyId;
     String externalMovieId;
     String trailersId;
}
