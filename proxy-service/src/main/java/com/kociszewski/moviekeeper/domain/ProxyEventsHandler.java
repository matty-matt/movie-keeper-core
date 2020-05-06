package com.kociszewski.moviekeeper.domain;

import com.kociszewski.moviekeeper.domain.commands.SaveCastDetailsCommand;
import com.kociszewski.moviekeeper.domain.commands.SaveMovieDetailsCommand;
import com.kociszewski.moviekeeper.domain.commands.SaveTrailersDetailsCommand;
import com.kociszewski.moviekeeper.domain.events.CastFetchDelegatedEvent;
import com.kociszewski.moviekeeper.domain.events.MovieFetchDelegatedEvent;
import com.kociszewski.moviekeeper.domain.events.TrailersFetchDelegatedEvent;
import com.kociszewski.moviekeeper.infrastructure.CastDTO;
import com.kociszewski.moviekeeper.infrastructure.ExternalMovie;
import com.kociszewski.moviekeeper.infrastructure.MovieState;
import com.kociszewski.moviekeeper.infrastructure.TrailerSectionDTO;
import com.kociszewski.moviekeeper.tmdb.NotFoundInExternalServiceException;
import com.kociszewski.moviekeeper.tmdb.TmdbService;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProxyEventsHandler {
    private final TmdbService tmdbService;
    private final CommandGateway commandGateway;

//    @EventHandler
//    public void on(MovieFetchDelegatedEvent event) {
//        ExternalMovie externalMovie;
//        try {
//            externalMovie = tmdbService.searchMovie(event.getSearchPhrase());
//        } catch (NotFoundInExternalServiceException e) {
//            externalMovie = ExternalMovie.builder().movieState(MovieState.NOT_FOUND_IN_EXTERNAL_SERVICE).build();
//        }
//        commandGateway.send(new SaveMovieDetailsCommand(event.getProxyId(), externalMovie));
//    }

    @EventHandler
    public void on(CastFetchDelegatedEvent event) {
        CastDTO castDTO = tmdbService.retrieveCast(event.getExternalMovieId());
        castDTO.setExternalMovieId(event.getExternalMovieId());
        commandGateway.send(new SaveCastDetailsCommand(event.getProxyId(), castDTO));
    }

    @EventHandler
    public void on(TrailersFetchDelegatedEvent event) {
        TrailerSectionDTO trailerSectionDTO = tmdbService.retrieveTrailers(event.getExternalMovieId());
        trailerSectionDTO.setExternalMovieId(event.getExternalMovieId());
        commandGateway.send(new SaveTrailersDetailsCommand(event.getProxyId(), trailerSectionDTO));
    }
}
