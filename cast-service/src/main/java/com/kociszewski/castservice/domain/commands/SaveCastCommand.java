package com.kociszewski.castservice.domain.commands;

import com.kociszewski.movieservice.infrastructure.cast.CastDTO;
import com.kociszewski.movieservice.shared.MovieId;
import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Value
public class SaveCastCommand {
    @TargetAggregateIdentifier
    private MovieId movieId;
    private CastDTO castDTO;
}
