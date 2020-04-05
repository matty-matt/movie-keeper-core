package com.kociszewski.proxyservice.domain;

import com.kociszewski.proxyservice.domain.commands.SaveMovieDetailsCommand;
import com.kociszewski.proxyservice.domain.events.MovieFetchDelegatedEvent;
import com.kociszewski.proxyservice.shared.ExternalMovie;
import com.kociszewski.proxyservice.shared.MovieState;
import com.kociszewski.proxyservice.tmdb.NotFoundInExternalServiceException;
import com.kociszewski.proxyservice.tmdb.TmdbService;
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
