package com.kociszewski.moviekeepercore.domain.movie;

import com.kociszewski.moviekeepercore.domain.ExternalService;
import com.kociszewski.moviekeepercore.domain.cast.commands.SaveCastCommand;
import com.kociszewski.moviekeepercore.domain.cast.events.CastFoundEvent;
import com.kociszewski.moviekeepercore.domain.movie.commands.SaveMovieCommand;
import com.kociszewski.moviekeepercore.domain.movie.events.MovieSearchDelegatedEvent;
import com.kociszewski.moviekeepercore.domain.movie.queries.GetMovieQuery;
import com.kociszewski.moviekeepercore.domain.trailer.events.TrailersFoundEvent;
import com.kociszewski.moviekeepercore.infrastructure.cast.CastDTO;
import com.kociszewski.moviekeepercore.shared.model.*;
import com.kociszewski.moviekeepercore.domain.trailer.commands.SaveTrailersCommand;
import com.kociszewski.moviekeepercore.infrastructure.movie.NotFoundInExternalServiceException;
import com.kociszewski.moviekeepercore.infrastructure.movie.MovieDTO;
import com.kociszewski.moviekeepercore.infrastructure.movie.MovieState;
import com.kociszewski.moviekeepercore.infrastructure.trailer.TrailerSectionDTO;
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
    private final ExternalService externalService;
    private final CommandGateway commandGateway;
    private final QueryUpdateEmitter queryUpdateEmitter;

    @EventHandler
    public void handle(MovieSearchDelegatedEvent event) {
            log.info("Handling {}, id={}", event.getClass().getSimpleName(), event.getMovieId().getId());
            try {
                ExternalMovie externalMovie = externalService.searchMovie(event.getSearchPhrase());
                commandGateway.send(new SaveMovieCommand(event.getMovieId(), externalMovie));
            } catch (NotFoundInExternalServiceException e) {
                queryUpdateEmitter.emit(GetMovieQuery.class, query -> true, new MovieDTO(MovieState.NOT_FOUND_IN_EXTERNAL_SERVICE));
            }
    }

    @EventHandler
    public void handle(TrailersFoundEvent event) {
        log.info("Handling {}, id={}", event.getClass().getSimpleName(), event.getMovieId().getId());
        TrailerSectionDTO trailers = getTrailerSectionDTO(event.getExternalMovieId(), event.getTrailerEntityId(), event.getMovieId());
        commandGateway.send(new SaveTrailersCommand(event.getMovieId(), trailers));
    }

    @EventHandler
    public void handle(CastFoundEvent event) {
        log.info("Handling {}, id={}", event.getClass().getSimpleName(), event.getMovieId().getId());
        CastDTO cast = getCastDTO(event.getExternalMovieId(), event.getCastEntityId(), event.getMovieId());
        commandGateway.send(new SaveCastCommand(event.getMovieId(), cast));
    }

    private TrailerSectionDTO getTrailerSectionDTO(ExternalMovieId externalMovieId, TrailerEntityId trailerEntityId, MovieId movieId) {
        TrailerSectionDTO trailers = externalService.retrieveTrailers(externalMovieId);
        trailers.setExternalMovieId(externalMovieId.getId());
        trailers.setAggregateId(trailerEntityId.getId());
        trailers.setMovieId(movieId.getId());
        return trailers;
    }

    private CastDTO getCastDTO(ExternalMovieId externalMovieId, CastEntityId castEntityId, MovieId movieId) {
        CastDTO cast = externalService.retrieveCast(externalMovieId);
        cast.setExternalMovieId(externalMovieId.getId());
        cast.setAggregateId(castEntityId.getId());
        cast.setMovieId(movieId.getId());
        return cast;
    }
}
