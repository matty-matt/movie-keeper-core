package com.kociszewski.castservice.domain.commands;

import com.kociszewski.castservice.infrastructure.CastDTO;
import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Value
public class SaveCastCommand {
    @TargetAggregateIdentifier
    private String movieId;
    private CastDTO castDTO;
}
