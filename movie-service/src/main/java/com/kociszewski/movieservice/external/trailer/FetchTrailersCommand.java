package com.kociszewski.movieservice.external.trailer;

import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Value
public class FetchTrailersCommand {
    @TargetAggregateIdentifier
    private String trailersId;
    private String externalMovieId;
}
