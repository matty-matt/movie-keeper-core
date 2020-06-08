package com.kociszewski.moviekeeper.domain;

import com.kociszewski.moviekeeper.domain.commands.*;
import com.kociszewski.moviekeeper.domain.events.*;
import com.kociszewski.moviekeeper.infrastructure.CastDTO;
import com.kociszewski.moviekeeper.infrastructure.ExternalMovie;
import com.kociszewski.moviekeeper.infrastructure.MovieState;
import com.kociszewski.moviekeeper.infrastructure.TrailerSectionDTO;
import com.kociszewski.moviekeeper.tmdb.NotFoundInExternalServiceException;
import com.kociszewski.moviekeeper.tmdb.TmdbService;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventhandling.gateway.EventGateway;
import org.springframework.stereotype.Component;

import static org.apache.logging.log4j.util.Strings.EMPTY;

@Component
@RequiredArgsConstructor
public class ProxyCommandHandler {

    private static final String PREFIX = "proxy_";

    private final TmdbService tmdbService;
    private final EventGateway eventGateway;

    @CommandHandler
    public void handle(FetchMovieDetailsCommand command) {
        ExternalMovie externalMovie;
        try {
            externalMovie = tmdbService.searchMovie(command.getSearchPhrase());
        } catch (NotFoundInExternalServiceException e) {
            externalMovie = ExternalMovie.builder().movieState(MovieState.NOT_FOUND_IN_EXTERNAL_SERVICE).build();
        }

        eventGateway.publish(new MovieDetailsEvent(command.getProxyId(), externalMovie));
    }

    @CommandHandler
    public void handle(FetchCastDetailsCommand command) {
        CastDTO castDTO = tmdbService.retrieveCast(command.getExternalMovieId());
        castDTO.setExternalMovieId(command.getExternalMovieId());
        castDTO.setAggregateId(command.getCastId());
        castDTO.setMovieId(command.getProxyId().replace(PREFIX, EMPTY));

        eventGateway.publish(new CastDetailsFetchedEvent(command.getProxyId(), castDTO));
    }

    @CommandHandler
    public void handle(FetchTrailersDetailsCommand command) {
        TrailerSectionDTO trailerSectionDTO = tmdbService.retrieveTrailers(command.getExternalMovieId());
        trailerSectionDTO.setExternalMovieId(command.getExternalMovieId());
        trailerSectionDTO.setAggregateId(command.getTrailersId());
        trailerSectionDTO.setMovieId(command.getProxyId().replace(PREFIX, EMPTY));

        eventGateway.publish(new TrailersDetailsFetchedEvent(command.getProxyId(), trailerSectionDTO));
    }
}
