package com.kociszewski.trailerservice.domain.commands;

import com.kociszewski.movieservice.shared.ExternalMovieId;
import com.kociszewski.movieservice.shared.MovieId;
import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Value
public class FindTrailersCommand {
    @TargetAggregateIdentifier
    private MovieId movieId;
    private ExternalMovieId externalMovieId;
}
