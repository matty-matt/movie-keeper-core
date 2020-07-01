package com.kociszewski.moviekeeper.domain;

import com.kociszewski.moviekeeper.domain.commands.CreateCastCommand;
import com.kociszewski.moviekeeper.domain.commands.CreateTrailersCommand;
import com.kociszewski.moviekeeper.domain.commands.DeleteCastCommand;
import com.kociszewski.moviekeeper.domain.commands.DeleteTrailersCommand;
import com.kociszewski.moviekeeper.domain.events.MovieDeletedEvent;
import com.kociszewski.moviekeeper.domain.events.TrailersAndCastSearchDelegatedEvent;
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
