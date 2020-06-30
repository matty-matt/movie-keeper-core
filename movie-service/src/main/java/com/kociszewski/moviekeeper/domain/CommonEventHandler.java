package com.kociszewski.moviekeeper.domain;

import com.kociszewski.moviekeeper.domain.commands.*;
import com.kociszewski.moviekeeper.domain.events.*;
import com.kociszewski.moviekeeper.infrastructure.MovieProjection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommonEventHandler {

    private final CommandGateway commandGateway;
    private final MovieProjection movieProjection;

    @EventHandler
    public void handle(TrailersAndCastSearchDelegatedEvent event) {
        log.info("[event-handler] Handling {}, movieId={}, castId={}, trailersId={}, externalId={}",
                event.getClass().getSimpleName(),
                event.getMovieId(),
                event.getCastId(),
                event.getTrailersId(),
                event.getExternalMovieId());
        var externalMovieId = event.getExternalMovieId();
        commandGateway.send(new CreateCastCommand(event.getCastId(), externalMovieId, event.getMovieId()));
        commandGateway.send(new CreateTrailersCommand(event.getTrailersId(), externalMovieId, event.getMovieId()));
    }

    @EventHandler
    public void handle(MovieDeletedEvent event) {
        log.info("[event-handler] Handling {}, movieId={}, castId={}, trailersId={}",
                event.getClass().getSimpleName(),
                event.getMovieId(),
                event.getCastId(),
                event.getTrailersId());
        commandGateway.send(new DeleteCastCommand(event.getCastId()));
        commandGateway.send(new DeleteTrailersCommand(event.getTrailersId()));
    }

    @EventHandler
    public void handle(MultipleMoviesRefreshedEvent event) {
        movieProjection.refreshMovies(event.getRefreshedMovies());
        event.getRefreshedMovies()
                .parallelStream()
                .forEach(movie -> commandGateway.send(new RefreshMovieCommand(movie.getAggregateId(), movie)));
    }
}
