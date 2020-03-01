package com.kociszewski.moviekeepercore.domain.cast.commands;

import com.kociszewski.moviekeepercore.infrastructure.cast.CastDTO;
import com.kociszewski.moviekeepercore.shared.model.MovieId;
import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Value
public class SaveCastCommand {
    @TargetAggregateIdentifier
    private MovieId movieId;
    private CastDTO castDTO;
}
