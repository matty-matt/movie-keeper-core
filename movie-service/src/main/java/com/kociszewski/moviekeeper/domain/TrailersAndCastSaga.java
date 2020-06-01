package com.kociszewski.moviekeeper.domain;

import com.kociszewski.moviekeeper.domain.commands.DeleteCastCommand;
import com.kociszewski.moviekeeper.domain.commands.DeleteTrailersCommand;
import com.kociszewski.moviekeeper.domain.commands.FindCastCommand;
import com.kociszewski.moviekeeper.domain.commands.FindTrailersCommand;
import com.kociszewski.moviekeeper.domain.events.*;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;

import static com.kociszewski.moviekeeper.domain.MovieSaga.*;
import static org.axonframework.modelling.saga.SagaLifecycle.associateWith;
import static org.axonframework.modelling.saga.SagaLifecycle.end;

@Saga
@Slf4j
public class TrailersAndCastSaga {
    private boolean castFound = false;
    private boolean trailersFound = false;
    private boolean castDeleted = false;
    private boolean trailersDeleted = false;

    @Autowired
    private CommandGateway commandGateway;

    @StartSaga
    @SagaEventHandler(associationProperty = MOVIE_ID)
    public void handle(TrailersAndCastSearchDelegatedEvent event) {
        log.info("[saga] Handling {}, movieId={}, castId={}, trailersId={}, externalId={}",
                event.getClass().getSimpleName(),
                event.getMovieId(),
                event.getCastId(),
                event.getTrailersId(),
                event.getExternalMovieId());
        var externalMovieId = event.getExternalMovieId();
        associateWith(TRAILERS_ID, event.getTrailersId());
        associateWith(CAST_ID, event.getCastId());
        commandGateway.send(new FindCastCommand(event.getCastId(), externalMovieId, event.getMovieId()));
        commandGateway.send(new FindTrailersCommand(event.getTrailersId(), externalMovieId, event.getMovieId()));
    }

    @SagaEventHandler(associationProperty = TRAILERS_ID)
    public void handle(TrailersSavedEvent event) {
        log.info("[saga] Trailers for movie saved");
        trailersFound = true;
        if (castFound) {
            end();
        }
    }

    @SagaEventHandler(associationProperty = CAST_ID)
    public void handle(CastSavedEvent event) {
        log.info("[saga] Cast for movie saved");
        castFound = true;
        if (trailersFound) {
            end();
        }
    }

    @SagaEventHandler(associationProperty = MOVIE_ID)
    public void handle(MovieDeletedEvent event) {
        log.info("[saga] Handling {}, id={}", event.getClass().getSimpleName(), event.getMovieId());
        associateWith(TRAILERS_ID, event.getTrailersId());
        associateWith(CAST_ID, event.getCastId());
        commandGateway.send(new DeleteCastCommand(event.getCastId()));
        commandGateway.send(new DeleteTrailersCommand(event.getTrailersId()));
    }

    @SagaEventHandler(associationProperty = TRAILERS_ID)
    public void handle(TrailersDeletedEvent event) {
        log.info("[saga] Trailers for movie deleted");
        trailersDeleted = true;
        if (castDeleted) {
            end();
        }
    }

    @SagaEventHandler(associationProperty = CAST_ID)
    public void handle(CastDeletedEvent event) {
        log.info("[saga] Cast for movie deleted");
        castDeleted = true;
        if (trailersDeleted) {
            end();
        }
    }
}
