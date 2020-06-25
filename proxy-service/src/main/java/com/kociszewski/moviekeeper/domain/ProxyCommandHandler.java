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
    public void handle(RefreshMoviesCommand command) {
        command.getMoviesToRefresh().forEach(movieId -> {
            VoteDTO vote = tmdbService.retrieveVote(movieId);
            String digitalReleaseDate = tmdbService.retrieveDigitalRelease(movieId);
            log.info("Fetching new avgVote and releaseDate for movie={}. New vote={}, new digitalReleaseDate={}", movieId, vote.getVoteAverage(), digitalReleaseDate);
        });
    }
}
