package com.kociszewski.moviekeepercore.domain.movie.commands;

import com.kociszewski.moviekeepercore.domain.movie.info.MovieId;
import com.kociszewski.moviekeepercore.shared.model.SearchPhrase;
import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Value
public class FindMovieCommand {
    @TargetAggregateIdentifier
    private MovieId movieId;
    private SearchPhrase phrase;
}
