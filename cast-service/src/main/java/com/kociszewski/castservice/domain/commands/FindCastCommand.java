package com.kociszewski.castservice.domain.commands;

import com.kociszewski.movieservice.shared.ExternalMovieId;
import com.kociszewski.movieservice.shared.MovieId;
import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Value
public class FindCastCommand {
    @TargetAggregateIdentifier
    private MovieId movieId;
    private ExternalMovieId externalMovieId;
}
