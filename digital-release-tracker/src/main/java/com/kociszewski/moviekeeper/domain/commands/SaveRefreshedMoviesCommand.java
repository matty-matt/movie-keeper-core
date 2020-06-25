package com.kociszewski.moviekeeper.domain.commands;

import com.kociszewski.moviekeeper.infrastructure.RefreshData;
import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.List;

@Value
public class SaveRefreshedMoviesCommand {
    @TargetAggregateIdentifier
    String refreshId;
    List<RefreshData> refreshedMovies;
}
