package com.kociszewski.moviekeeper.domain.commands;

import lombok.*;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Value
public class FindCastCommand {
    @TargetAggregateIdentifier
    String castId;
    String externalMovieId;
    String dupaId;
}
