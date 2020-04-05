package com.kociszewski.moviekeeper.domain;

import com.kociszewski.moviekeeper.domain.commands.FetchMovieDetailsCommand;
import com.kociszewski.moviekeeper.domain.commands.MovieDetailsFetchedEvent;
import com.kociszewski.moviekeeper.domain.events.CastSearchDelegatedEvent;
import com.kociszewski.moviekeeper.domain.events.TrailersSearchDelegatedEvent;
import com.kociszewski.moviekeeper.infrastructure.MovieDTO;
import com.kociszewski.moviekeeper.domain.commands.SaveMovieCommand;
import com.kociszewski.moviekeeper.domain.queries.GetMovieQuery;
import com.kociszewski.moviekeeper.external.cast.FetchCastCommand;
import com.kociszewski.moviekeeper.domain.events.MovieSearchDelegatedEvent;
import com.kociszewski.moviekeeper.external.trailer.FetchTrailersCommand;
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
import static org.axonframework.modelling.saga.SagaLifecycle.end;

@Saga
@Slf4j
public class MovieSaga {

    public static final String TRAILER_PREFIX = "trailer_";
    public static final String CAST_PREFIX = "cast_";
    public static final String PROXY_PREFIX = "proxy_";

    @Autowired
    private transient CommandGateway commandGateway;

    @Autowired
    private QueryUpdateEmitter queryUpdateEmitter;

    private String movieId;

    @StartSaga
    @SagaEventHandler(associationProperty = "movieId")
    public void handle(MovieSearchDelegatedEvent event) {
            log.info("[saga] Handling {}, id={}", event.getClass().getSimpleName(), event.getMovieId());
            movieId = event.getMovieId();
            String proxyId = PROXY_PREFIX.concat(movieId);
            associateWith("proxyId", proxyId);
            commandGateway.send(new FetchMovieDetailsCommand(proxyId, event.getSearchPhrase()));
    }

    @SagaEventHandler(associationProperty = "proxyId")
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

    @SagaEventHandler(associationProperty = "movieId")
    public void handle(CastSearchDelegatedEvent event) {
        log.info("[saga] Handling {}, id={}", event.getClass().getSimpleName(), event.getMovieId());

        String castId = CAST_PREFIX.concat(movieId);
        associateWith("castId", castId);
        commandGateway.send(new FetchCastCommand(castId, event.getExternalMovieId()));
    }

    @SagaEventHandler(associationProperty = "movieId")
    public void handle(TrailersSearchDelegatedEvent event) {
        log.info("[saga] Handling {}, id={}", event.getClass().getSimpleName(), event.getMovieId());

        String trailersId = TRAILER_PREFIX.concat(movieId);
        associateWith("trailersId", trailersId);
        commandGateway.send(new FetchTrailersCommand(trailersId, event.getExternalMovieId()));
    }

//    @EventHandler
//    public void handle(TrailersFoundEvent event) {
//        log.info("Handling {}, id={}", event.getClass().getSimpleName(), event.getMovieId().getId());
//        TrailerSectionDTO trailers = getTrailerSectionDTO(event.getExternalMovieId(), event.getTrailerEntityId(), event.getMovieId());
//        commandGateway.send(new SaveTrailersCommand(event.getMovieId(), trailers));
//    }
//
//    @EventHandler
//    public void handle(CastFoundEvent event) {
//        log.info("Handling {}, id={}", event.getClass().getSimpleName(), event.getMovieId().getId());
//        CastDTO cast = getCastDTO(event.getExternalMovieId(), event.getCastEntityId(), event.getMovieId());
//        commandGateway.send(new SaveCastCommand(event.getMovieId(), cast));
//    }

//    private TrailerSectionDTO getTrailerSectionDTO(ExternalMovieId externalMovieId, TrailerEntityId trailerEntityId, MovieId movieId) {
//        TrailerSectionDTO trailers = externalService.retrieveTrailers(externalMovieId);
//        trailers.setExternalMovieId(externalMovieId.getId());
//        trailers.setAggregateId(trailerEntityId.getId());
//        trailers.setMovieId(movieId.getId());
//        return trailers;
//    }
//
//    private CastDTO getCastDTO(ExternalMovieId externalMovieId, CastEntityId castEntityId, MovieId movieId) {
//        CastDTO cast = externalService.retrieveCast(externalMovieId);
//        cast.setExternalMovieId(externalMovieId.getId());
//        cast.setAggregateId(castEntityId.getId());
//        cast.setMovieId(movieId.getId());
//        return cast;
//    }
}
