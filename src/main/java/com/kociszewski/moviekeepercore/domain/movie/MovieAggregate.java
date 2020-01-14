package com.kociszewski.moviekeepercore.domain.movie;

import com.kociszewski.moviekeepercore.domain.ExternalId;
import com.kociszewski.moviekeepercore.domain.movie.commands.AddMovieCommand;
import com.kociszewski.moviekeepercore.domain.movie.events.MovieAddedEvent;
import com.kociszewski.moviekeepercore.domain.movie.info.MovieId;
import com.kociszewski.moviekeepercore.domain.movie.info.MovieInfo;
import com.kociszewski.moviekeepercore.domain.movie.info.Watched;
import com.kociszewski.moviekeepercore.domain.movie.info.releases.ReleaseDate;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;

import java.util.Date;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

@Aggregate
public class MovieAggregate {

    @AggregateIdentifier
    private MovieId movieId;
    private ExternalId externalId;
    private MovieInfo movieInfo;
    private ReleaseDate releaseDate;
    private Watched watched;
    private Date insertionDate;
    private Date lastRefreshDate;

    @CommandHandler
    public MovieAggregate(AddMovieCommand command) {
        apply(new MovieAddedEvent());
    }

}
