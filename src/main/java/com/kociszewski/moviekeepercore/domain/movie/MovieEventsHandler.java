package com.kociszewski.moviekeepercore.domain.movie;

import com.kociszewski.moviekeepercore.domain.ExternalService;
import com.kociszewski.moviekeepercore.domain.movie.commands.SaveMovieCommand;
import com.kociszewski.moviekeepercore.domain.movie.events.MovieSearchDelegatedEvent;
import com.kociszewski.moviekeepercore.domain.movie.queries.FindMovieQuery;
import com.kociszewski.moviekeepercore.infrastructure.exception.NotFoundInExternalServiceException;
import com.kociszewski.moviekeepercore.infrastructure.persistence.MovieDTO;
import com.kociszewski.moviekeepercore.shared.model.ExternalMovie;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MovieEventsHandler {
    private final ExternalService externalService;
    private final CommandGateway commandGateway;
    private final QueryUpdateEmitter queryUpdateEmitter;

    @EventHandler
    public void on(MovieSearchDelegatedEvent event) {
        try {
            ExternalMovie externalMovie = externalService.searchMovie(event.getSearchPhrase());
            commandGateway.sendAndWait(new SaveMovieCommand(event.getMovieId(), externalMovie));
        } catch (NotFoundInExternalServiceException e) {
            queryUpdateEmitter.emit(FindMovieQuery.class, query -> true, new MovieDTO());
        }
    }
}
