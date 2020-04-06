package com.kociszewski.moviekeeper.domain;

import com.kociszewski.moviekeeper.domain.commands.FetchCastDetailsCommand;
import com.kociszewski.moviekeeper.domain.commands.FetchMovieDetailsCommand;
import com.kociszewski.moviekeeper.domain.commands.SaveCastDetailsCommand;
import com.kociszewski.moviekeeper.domain.events.CastFetchDelegatedEvent;
import com.kociszewski.moviekeeper.domain.events.CastDetailsFetchedEvent;
import com.kociszewski.moviekeeper.domain.events.MovieFetchDelegatedEvent;
import com.kociszewski.moviekeeper.domain.commands.SaveMovieDetailsCommand;
import com.kociszewski.moviekeeper.domain.events.MovieDetailsFetchedEvent;
import com.kociszewski.moviekeeper.infrastructure.ExternalMovie;
import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

@Aggregate
@NoArgsConstructor
class ExternalMovieAggregate {

    @AggregateIdentifier
    private String proxyId;

    private String searchPhrase;
    private ExternalMovie externalMovie;
    private String castId;

    @CommandHandler
    ExternalMovieAggregate(FetchMovieDetailsCommand command) {
        apply(new MovieFetchDelegatedEvent(command.getProxyId(), command.getSearchPhrase()));
    }

    @EventSourcingHandler
    public void on(MovieFetchDelegatedEvent event) {
        this.proxyId = event.getProxyId();
        this.searchPhrase = event.getSearchPhrase();
    }
    
    @CommandHandler
    public void handle(SaveMovieDetailsCommand command) {
        if (this.externalMovie == null) {
            apply(new MovieDetailsFetchedEvent(command.getProxyId(), command.getExternalMovie()));
        }
    }
    
    @EventSourcingHandler
    public void on(MovieDetailsFetchedEvent event) {
        this.externalMovie = event.getExternalMovie();
    }

    @CommandHandler
    public void handle(FetchCastDetailsCommand command) {
        apply(new CastFetchDelegatedEvent(command.getProxyId(), command.getExternalMovieId(), command.getCastId()));
    }

    @EventSourcingHandler
    public void on(CastFetchDelegatedEvent event) {
        this.castId = event.getCastId();
    }

    @CommandHandler
    public void handle(SaveCastDetailsCommand command) {
        apply(new CastDetailsFetchedEvent(command.getProxyId(), command.getCastDTO()));
    }
}
