package com.kociszewski.moviekeepercore.domain.movie.commands;

import com.kociszewski.moviekeepercore.shared.model.TrailerEntityId;
import com.kociszewski.moviekeepercore.shared.model.MovieId;
import com.kociszewski.moviekeepercore.shared.model.SearchPhrase;
import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Value
public class FindMovieCommand {
    @TargetAggregateIdentifier
    private MovieId movieId;
    private TrailerEntityId trailerEntityId;
    private SearchPhrase phrase;
}
