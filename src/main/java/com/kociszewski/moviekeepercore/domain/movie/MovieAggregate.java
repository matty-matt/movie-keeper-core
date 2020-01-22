package com.kociszewski.moviekeepercore.domain.movie;

import com.kociszewski.moviekeepercore.domain.movie.commands.FindMovieCommand;
import com.kociszewski.moviekeepercore.domain.movie.commands.SetExternalMovieIdCommand;
import com.kociszewski.moviekeepercore.domain.movie.events.MovieIdFoundEvent;
import com.kociszewski.moviekeepercore.domain.movie.events.SearchDelegatedEvent;
import com.kociszewski.moviekeepercore.domain.movie.info.MovieId;
import com.kociszewski.moviekeepercore.domain.movie.info.MovieInfo;
import com.kociszewski.moviekeepercore.domain.movie.info.SearchPhrase;
import com.kociszewski.moviekeepercore.domain.movie.info.Watched;
import com.kociszewski.moviekeepercore.domain.movie.info.releases.Releases;
import com.kociszewski.moviekeepercore.shared.model.ExternalMovieId;
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
    private ExternalMovieId externalMovieId;
    private MovieInfo movieInfo;
    private Releases releases;
    private Watched watched;
    private Date insertionDate;
    private Date lastRefreshDate;
    private SearchPhrase searchPhrase;

    private MovieAggregate() {}

    @CommandHandler
    public MovieAggregate(FindMovieCommand command) {
        apply(new SearchDelegatedEvent(command.getMovieId(), command.getPhrase()));
    }

    @EventSourcingHandler
    private void on(SearchDelegatedEvent event) {
        System.out.println(">>>>>> Handling SearchDelegatedEvent");
        this.movieId = event.getMovieId();
        this.searchPhrase = event.getSearchPhrase();
    }

    @CommandHandler
    public void handle(SetExternalMovieIdCommand command) {
        apply(new MovieIdFoundEvent(command.getMovieId(), command.getExternalMovieId()));
    }

    @EventSourcingHandler
    private void on(MovieIdFoundEvent event) {
        System.out.println(">>>>>>> Handling MovieIdFoundEvent");
        this.externalMovieId = event.getExternalMovieId();
    }

}
