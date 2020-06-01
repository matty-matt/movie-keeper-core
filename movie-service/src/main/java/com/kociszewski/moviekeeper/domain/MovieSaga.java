package com.kociszewski.moviekeeper.domain;

import com.kociszewski.moviekeeper.domain.commands.FetchMovieDetailsCommand;
import com.kociszewski.moviekeeper.domain.commands.FindCastCommand;
import com.kociszewski.moviekeeper.domain.commands.FindTrailersCommand;
import com.kociszewski.moviekeeper.domain.events.*;
import com.kociszewski.moviekeeper.infrastructure.MovieDTO;
import com.kociszewski.moviekeeper.domain.commands.SaveMovieCommand;
import com.kociszewski.moviekeeper.domain.queries.GetMovieQuery;
import com.kociszewski.moviekeeper.infrastructure.MovieState;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.SagaLifecycle;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.axonframework.modelling.saga.SagaLifecycle.associateWith;
import static org.axonframework.modelling.saga.SagaLifecycle.end;

@Saga
@Slf4j
public class MovieSaga {

    private static final String PROXY_PREFIX = "proxy_";
    private static final String CAST_ID = "castId";
    private static final String TRAILERS_ID = "trailersId";
    private static final String MOVIE_ID = "movieId";
    private static final String PROXY_ID = "proxyId";

    @Autowired
    private CommandGateway commandGateway;

    @Autowired
    private QueryUpdateEmitter queryUpdateEmitter;

    private String movieId;
    private boolean castFound = false;
    private boolean trailersFound = false;

    @StartSaga
    @SagaEventHandler(associationProperty = MOVIE_ID)
    public void handle(MovieSearchDelegatedEvent event) {
        log.info("[saga] Handling {}, id={}", event.getClass().getSimpleName(), event.getMovieId());
        movieId = event.getMovieId();
        String proxyId = PROXY_PREFIX.concat(movieId);
        associateWith("proxyId", proxyId);
        commandGateway.send(new FetchMovieDetailsCommand(proxyId, event.getSearchPhrase()));
    }

    @SagaEventHandler(associationProperty = PROXY_ID)
    @EndSaga
    public void handle(MovieDetailsFetchedEvent event) {
        log.info("[saga] Handling {}, id={}", event.getClass().getSimpleName(), event.getProxyId());

        var movie = event.getExternalMovie();
        if (MovieState.NOT_FOUND_IN_EXTERNAL_SERVICE == movie.getMovieState()) {
            queryUpdateEmitter.emit(GetMovieQuery.class, query -> true, new MovieDTO(MovieState.NOT_FOUND_IN_EXTERNAL_SERVICE));
            end();
        } else {
            commandGateway.send(new SaveMovieCommand(movieId, event.getExternalMovie()));
        }
    }

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
}
