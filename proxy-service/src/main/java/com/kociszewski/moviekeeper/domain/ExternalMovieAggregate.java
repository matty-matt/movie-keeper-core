package com.kociszewski.moviekeeper.domain;

import com.kociszewski.moviekeeper.domain.commands.*;
import com.kociszewski.moviekeeper.domain.events.*;
import com.kociszewski.moviekeeper.infrastructure.CastDTO;
import com.kociszewski.moviekeeper.infrastructure.ExternalMovie;
import com.kociszewski.moviekeeper.infrastructure.TrailerSectionDTO;
import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;

import static org.apache.logging.log4j.util.Strings.EMPTY;
import static org.axonframework.modelling.command.AggregateLifecycle.apply;

@Aggregate
@NoArgsConstructor
class ExternalMovieAggregate {

    private static final String PREFIX = "proxy_";

    @AggregateIdentifier
    private String proxyId;

    private String searchPhrase;
    private ExternalMovie externalMovie;
    private String castId;
    private String trailersId;

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
        CastDTO castDTO = command.getCastDTO();
        castDTO.setAggregateId(castId);
        castDTO.setMovieId(proxyId.replace(PREFIX, EMPTY));
        apply(new CastDetailsFetchedEvent(command.getProxyId(), castDTO));
    }

    @CommandHandler
    public void handle(FetchTrailersDetailsCommand command) {
        apply(new TrailersFetchDelegatedEvent(command.getProxyId(), command.getExternalMovieId(), command.getTrailersId()));
    }

    @EventSourcingHandler
    public void on(TrailersFetchDelegatedEvent event) {
        this.trailersId = event.getTrailersId();
    }

    @CommandHandler
    public void handle(SaveTrailersDetailsCommand command) {
        TrailerSectionDTO trailerSectionDTO = command.getTrailerSectionDTO();
        trailerSectionDTO.setAggregateId(trailersId);
        trailerSectionDTO.setMovieId(proxyId.replace(PREFIX, EMPTY));
        apply(new TrailersDetailsFetchedEvent(command.getProxyId(), trailerSectionDTO));
    }

}
