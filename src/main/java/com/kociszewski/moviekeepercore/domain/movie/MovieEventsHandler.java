package com.kociszewski.moviekeepercore.domain.movie;

import com.kociszewski.moviekeepercore.domain.ExternalService;
import com.kociszewski.moviekeepercore.domain.movie.commands.SaveMovieCommand;
import com.kociszewski.moviekeepercore.domain.movie.events.MovieSearchDelegatedEvent;
import com.kociszewski.moviekeepercore.domain.movie.queries.GetMovieQuery;
import com.kociszewski.moviekeepercore.shared.model.TrailerEntityId;
import com.kociszewski.moviekeepercore.domain.trailer.commands.SaveTrailersCommand;
import com.kociszewski.moviekeepercore.infrastructure.movie.NotFoundInExternalServiceException;
import com.kociszewski.moviekeepercore.infrastructure.movie.MovieDTO;
import com.kociszewski.moviekeepercore.infrastructure.movie.MovieState;
import com.kociszewski.moviekeepercore.infrastructure.trailer.TrailerSectionDTO;
import com.kociszewski.moviekeepercore.shared.model.ExternalMovie;
import com.kociszewski.moviekeepercore.shared.model.MovieId;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MovieEventsHandler {
    private final ExternalService externalService;
    private final CommandGateway commandGateway;
    private final QueryUpdateEmitter queryUpdateEmitter;

    @EventHandler
    public void on(MovieSearchDelegatedEvent event) {
        try {
            ExternalMovie externalMovie = externalService.searchMovie(event.getMovieId(), event.getSearchPhrase());
            commandGateway.sendAndWait(new SaveMovieCommand(event.getMovieId(), externalMovie));

            TrailerSectionDTO trailers = getTrailerSectionDTO(externalMovie, event.getTrailerEntityId(), event.getMovieId());
            commandGateway.sendAndWait(new SaveTrailersCommand(event.getMovieId(), trailers));

        } catch (NotFoundInExternalServiceException e) {
            queryUpdateEmitter.emit(GetMovieQuery.class, query -> true, new MovieDTO(MovieState.NOT_FOUND_IN_EXTERNAL_SERVICE));
        }
    }

    private TrailerSectionDTO getTrailerSectionDTO(ExternalMovie externalMovie, TrailerEntityId trailerEntityId, MovieId movieId) {
        TrailerSectionDTO trailers = externalService.retrieveTrailers(externalMovie.getExternalMovieId());
        trailers.setExternalMovieId(externalMovie.getExternalMovieId().getId());
        trailers.setAggregateId(trailerEntityId.getId());
        trailers.setMovieId(movieId.getId());
        return trailers;
    }
}
