package com.kociszewski.moviekeeper.domain.commands;

import com.kociszewski.moviekeeper.infrastructure.CastDTO;
import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Value
public class SaveCastDetailsCommand {
    @TargetAggregateIdentifier
    private String proxyId;
    private CastDTO castDTO;
}
