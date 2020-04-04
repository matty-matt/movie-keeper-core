package com.kociszewski.movieservice.domain.commands;

import com.kociszewski.movieservice.shared.CastEntityId;
import com.kociszewski.movieservice.shared.TrailerEntityId;
import com.kociszewski.movieservice.shared.MovieId;
import com.kociszewski.movieservice.shared.SearchPhrase;
import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Value
public class FindMovieCommand {
    @TargetAggregateIdentifier
    private MovieId movieId;
    private TrailerEntityId trailerEntityId;
    private CastEntityId castEntityId;
    private SearchPhrase phrase;
}
