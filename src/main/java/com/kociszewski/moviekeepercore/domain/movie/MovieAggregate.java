package com.kociszewski.moviekeepercore.domain.movie;

import com.kociszewski.moviekeepercore.domain.ExternalId;
import com.kociszewski.moviekeepercore.domain.movie.commands.FindMovieCommand;
import com.kociszewski.moviekeepercore.domain.movie.events.SearchDelegatedEvent;
import com.kociszewski.moviekeepercore.domain.movie.info.MovieId;
import com.kociszewski.moviekeepercore.domain.movie.info.MovieInfo;
import com.kociszewski.moviekeepercore.domain.movie.info.Watched;
import com.kociszewski.moviekeepercore.domain.movie.info.releases.Releases;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
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
    private Releases releases;
    private Watched watched;
    private Date insertionDate;
    private Date lastRefreshDate;

    @CommandHandler
    private MovieAggregate(FindMovieCommand command) {
        apply(new SearchDelegatedEvent(command.getMovieId(), command.getTitle()));
    }

    @EventSourcingHandler
    private void on(SearchDelegatedEvent event) {
        this.movieId = event.getMovieId();
    }

}
