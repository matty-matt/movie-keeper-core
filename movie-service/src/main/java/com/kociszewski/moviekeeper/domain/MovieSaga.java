package com.kociszewski.moviekeeper.domain;

import com.kociszewski.moviekeeper.domain.commands.FetchMovieDetailsCommand;
import com.kociszewski.moviekeeper.domain.events.*;
import com.kociszewski.moviekeeper.infrastructure.MovieDTO;
import com.kociszewski.moviekeeper.domain.commands.SaveMovieCommand;
import com.kociszewski.moviekeeper.domain.queries.GetMovieQuery;
import com.kociszewski.moviekeeper.infrastructure.MovieState;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;

import static org.axonframework.modelling.saga.SagaLifecycle.associateWith;

@Saga
@Slf4j
public class MovieSaga {

    private static final String PROXY_PREFIX = "proxy_";
    private static final String PROXY_ID = "proxyId";
    public static final String CAST_ID = "castId";
    public static final String TRAILERS_ID = "trailersId";
    public static final String MOVIE_ID = "movieId";

    //TODO spróbować przez RequiredArgsConstructor i @Component
    @Autowired
    private CommandGateway commandGateway;

    @Autowired
    private QueryUpdateEmitter queryUpdateEmitter;

    private String movieId;

    @StartSaga
    @SagaEventHandler(associationProperty = MOVIE_ID)
    public void handle(MovieCreatedEvent event) {
        log.info("[saga] Handling {}, id={}", event.getClass().getSimpleName(), event.getMovieId());
        movieId = event.getMovieId();
        String proxyId = PROXY_PREFIX.concat(movieId);
        associateWith("proxyId", proxyId);
        commandGateway.send(new FetchMovieDetailsCommand(proxyId, event.getSearchPhrase()));
    }

    @SagaEventHandler(associationProperty = PROXY_ID)
    @EndSaga
    public void handle(MovieDetailsEvent event) {
        log.info("[saga] Handling {}, id={}", event.getClass().getSimpleName(), event.getProxyId());

        var movie = event.getExternalMovie();
        if (MovieState.NOT_FOUND_IN_EXTERNAL_SERVICE == movie.getMovieState()) {
            queryUpdateEmitter.emit(GetMovieQuery.class, query -> true, new MovieDTO(MovieState.NOT_FOUND_IN_EXTERNAL_SERVICE));
        } else {
            commandGateway.send(new SaveMovieCommand(movieId, event.getExternalMovie()));
        }
    }
}
