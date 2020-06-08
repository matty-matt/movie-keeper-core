package com.kociszewski.moviekeeper.domain;

import com.kociszewski.moviekeeper.domain.commands.DeleteCastCommand;
import com.kociszewski.moviekeeper.domain.commands.DeleteTrailersCommand;
import com.kociszewski.moviekeeper.domain.commands.CreateCastCommand;
import com.kociszewski.moviekeeper.domain.commands.FindTrailersCommand;
import com.kociszewski.moviekeeper.domain.events.*;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TrailersAndCastEventHandler {

    @Autowired
    private CommandGateway commandGateway;

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
        commandGateway.send(new FindTrailersCommand(event.getTrailersId(), externalMovieId, event.getMovieId()));
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
