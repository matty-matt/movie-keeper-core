package com.kociszewski.movieservice.domain;

import com.kociszewski.movieservice.domain.commands.SaveMovieCommand;
import com.kociszewski.movieservice.domain.events.MovieSearchDelegatedEvent;
import com.kociszewski.movieservice.domain.queries.GetMovieQuery;
import com.kociszewski.movieservice.infrastructure.MovieDTO;
import com.kociszewski.movieservice.infrastructure.MovieState;
import com.kociszewski.movieservice.shared.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovieEventsHandler {
    private final CommandGateway commandGateway;
    private final QueryUpdateEmitter queryUpdateEmitter;

    @EventHandler
    public void handle(MovieSearchDelegatedEvent event) {
            log.info("Handling {}, id={}", event.getClass().getSimpleName(), event.getMovieId());
//            try {
//                ExternalMovie externalMovie = externalService.searchMovie(event.getSearchPhrase());
//                commandGateway.send(new SaveMovieCommand(event.getMovieId(), externalMovie));
//            } catch (NotFoundInExternalServiceException e) {
//                queryUpdateEmitter.emit(GetMovieQuery.class, query -> true, new MovieDTO(MovieState.NOT_FOUND_IN_EXTERNAL_SERVICE));
//            }
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
