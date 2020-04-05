package com.kociszewski.movieservice.domain.commands;

import com.kociszewski.movieservice.shared.SearchPhrase;
import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Value
public class FindMovieCommand {
    @TargetAggregateIdentifier
    private String movieId;
    private String trailerEntityId;
    private String castEntityId;
    private SearchPhrase phrase;
}
