package com.kociszewski.moviekeeper.domain.commands;

import lombok.*;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Value
public class FindCastCommand {
    @TargetAggregateIdentifier
    private String castId;
    private String externalMovieId;
    private String movieId;
}