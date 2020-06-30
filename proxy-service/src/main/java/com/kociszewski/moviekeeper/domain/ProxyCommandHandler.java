package com.kociszewski.moviekeeper.domain;

import com.kociszewski.moviekeeper.domain.commands.*;
import com.kociszewski.moviekeeper.domain.events.*;
import com.kociszewski.moviekeeper.infrastructure.*;
import com.kociszewski.moviekeeper.tmdb.NotFoundInExternalServiceException;
import com.kociszewski.moviekeeper.tmdb.TmdbService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventhandling.gateway.EventGateway;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static org.apache.logging.log4j.util.Strings.EMPTY;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProxyCommandHandler {

    private static final String PREFIX = "proxy_";

    private final TmdbService tmdbService;
    private final EventGateway eventGateway;

    @CommandHandler
    public void handle(FetchMovieDetailsCommand command) {
        ExternalMovie externalMovie;
        try {
            externalMovie = tmdbService.searchMovie(command.getSearchPhrase());
        } catch (NotFoundInExternalServiceException e) {
            externalMovie = ExternalMovie.builder().movieState(MovieState.NOT_FOUND_IN_EXTERNAL_SERVICE).build();
        }

        eventGateway.publish(new MovieDetailsEvent(command.getProxyId(), externalMovie));
    }

    @CommandHandler
    public void handle(FetchCastCommand command) {
        CastDTO castDTO = tmdbService.retrieveCast(command.getExternalMovieId());
        castDTO.setExternalMovieId(command.getExternalMovieId());
        castDTO.setAggregateId(command.getCastId());
        castDTO.setMovieId(command.getProxyId().replace(PREFIX, EMPTY));

        eventGateway.publish(new CastDetailsEvent(command.getProxyId(), castDTO));
    }

    @CommandHandler
    public void handle(FetchTrailersCommand command) {
        TrailerSectionDTO trailerSectionDTO = tmdbService.retrieveTrailers(command.getExternalMovieId());
        trailerSectionDTO.setExternalMovieId(command.getExternalMovieId());
        trailerSectionDTO.setAggregateId(command.getTrailersId());
        trailerSectionDTO.setMovieId(command.getProxyId().replace(PREFIX, EMPTY));

        eventGateway.publish(new TrailersDetailsEvent(command.getProxyId(), trailerSectionDTO));
    }


    @CommandHandler
    public void handle(FetchRefreshData command) {
        List<RefreshData> refreshedMovies = command.getMoviesToRefresh()
                .parallelStream()
                .map(movie -> {
                    VoteDTO voteObject = tmdbService.retrieveVote(movie.getExternalMovieId());
                    return RefreshData.builder()
                            .movieId(movie.getExternalMovieId())
                            .aggregateId(movie.getAggregateId())
                            .averageVote(voteObject.getVoteAverage())
                            .voteCount(voteObject.getVoteCount())
                            .digitalReleaseDate(tmdbService.retrieveDigitalRelease(movie.getExternalMovieId()))
                            .build();
                })
                .peek(rd -> log.info("Fetched on refresh, {}", rd))
                .collect(Collectors.toList());
        eventGateway.publish(new MoviesRefreshDataEvent(command.getProxyId(), refreshedMovies));
    }
}
