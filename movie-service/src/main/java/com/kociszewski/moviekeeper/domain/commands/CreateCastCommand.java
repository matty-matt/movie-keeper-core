package com.kociszewski.moviekeeper.domain.commands;

import lombok.*;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Value
public class CreateCastCommand {
    @TargetAggregateIdentifier
    String castId;
    String externalMovieId;
    String movieId;
}
