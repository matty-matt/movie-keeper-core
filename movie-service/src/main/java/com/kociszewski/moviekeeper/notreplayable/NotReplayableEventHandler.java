package com.kociszewski.moviekeeper.notreplayable;

import com.kociszewski.moviekeeper.domain.commands.*;
import com.kociszewski.moviekeeper.domain.events.MovieDeletedEvent;
import com.kociszewski.moviekeeper.domain.events.MoviesRefreshedEvent;
import com.kociszewski.moviekeeper.domain.events.MultipleMoviesRefreshedEvent;
import com.kociszewski.moviekeeper.domain.events.TrailersAndCastSearchDelegatedEvent;
import com.kociszewski.moviekeeper.infrastructure.MovieDTO;
import com.kociszewski.moviekeeper.infrastructure.MovieProjection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.gateway.EventGateway;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class NotReplayableEventHandler {
    private final EventGateway eventGateway;
    private final MovieProjection movieProjection;
    private final CommandGateway commandGateway;

    @EventHandler
    public void handle(MultipleMoviesRefreshedEvent event) {
        List<MovieDTO> refreshedMovies = movieProjection.refreshMovies(event.getRefreshedMovies());
        eventGateway.publish(new MoviesRefreshedEvent(refreshedMovies));
        event.getRefreshedMovies()
                .parallelStream()
                .forEach(movie -> commandGateway.send(new RefreshMovieCommand(movie.getAggregateId(), movie)));
    }

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
}
