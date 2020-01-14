package com.kociszewski.moviekeepercore.domain.movie;

import com.kociszewski.moviekeepercore.domain.ExternalId;
import com.kociszewski.moviekeepercore.domain.movie.commands.SearchMovieCommand;
import com.kociszewski.moviekeepercore.domain.movie.events.MovieQueriedForSearch;
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
    public MovieAggregate(SearchMovieCommand command) {
        apply(new MovieQueriedForSearch(command.getMovieId(), command.getTitle()));
    }

    @EventSourcingHandler
    public void on(MovieQueriedForSearch event) {
        this.movieId = event.getMovieId();
        // TODO search for movie
    }

}
