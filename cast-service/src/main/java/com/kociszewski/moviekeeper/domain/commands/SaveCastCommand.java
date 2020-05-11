package com.kociszewski.moviekeeper.domain.commands;

import com.kociszewski.moviekeeper.infrastructure.CastDTO;
import lombok.*;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Value
public class SaveCastCommand {
    @TargetAggregateIdentifier
    String castId;
    CastDTO castDTO;
}
