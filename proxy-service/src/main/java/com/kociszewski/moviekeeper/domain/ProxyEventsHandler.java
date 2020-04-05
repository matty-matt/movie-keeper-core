package com.kociszewski.moviekeeper.domain;

import com.kociszewski.moviekeeper.domain.commands.SaveMovieDetailsCommand;
import com.kociszewski.moviekeeper.domain.events.MovieFetchDelegatedEvent;
import com.kociszewski.moviekeeper.shared.ExternalMovie;
import com.kociszewski.moviekeeper.shared.MovieState;
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

    @EventHandler
    public void on(MovieFetchDelegatedEvent event) {
        ExternalMovie externalMovie;
        try {
            externalMovie = tmdbService.searchMovie(event.getSearchPhrase());
        } catch (NotFoundInExternalServiceException e) {
            externalMovie = ExternalMovie.builder().movieState(MovieState.NOT_FOUND_IN_EXTERNAL_SERVICE).build();
        }
        commandGateway.send(new SaveMovieDetailsCommand(event.getProxyId(), externalMovie));
    }
}
