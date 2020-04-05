package com.kociszewski.proxyservice.domain;

import com.kociszewski.proxyservice.domain.commands.FetchMovieDetailsCommand;
import com.kociszewski.proxyservice.domain.commands.SaveMovieDetailsCommand;
import com.kociszewski.proxyservice.domain.events.MovieDetailsFetchedEvent;
import com.kociszewski.proxyservice.domain.events.MovieFetchDelegatedEvent;
import com.kociszewski.proxyservice.shared.ExternalMovie;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

@Aggregate
@NoArgsConstructor
public class ExternalMovieAggregate {

    @AggregateIdentifier
    private String proxyId;
    private String searchPhrase;
    private ExternalMovie externalMovie;

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
        apply(new MovieDetailsFetchedEvent(command.getProxyId(), command.getExternalMovie()));
    }
    
    @EventSourcingHandler
    public void on(MovieDetailsFetchedEvent event) {
        this.externalMovie = event.getExternalMovie();
    }
}
