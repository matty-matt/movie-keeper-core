package com.kociszewski.moviekeeper.domain;

import com.google.protobuf.Empty;
import com.kociszewski.moviekeeper.domain.commands.FetchMovieDetailsCommand;
import com.kociszewski.moviekeeper.domain.commands.SaveMovieCommand;
import com.kociszewski.moviekeeper.domain.commands.SaveMovieDetailsCommand;
import com.kociszewski.moviekeeper.domain.events.MovieDetailsFetchedEvent;
import com.kociszewski.moviekeeper.infrastructure.ExternalMovie;
import com.kociszewski.moviekeeper.infrastructure.MovieState;
import com.kociszewski.moviekeeper.tmdb.NotFoundInExternalServiceException;
import com.kociszewski.moviekeeper.tmdb.TmdbService;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.gateway.EventGateway;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.stereotype.Component;

import static org.apache.logging.log4j.util.Strings.EMPTY;
import static org.axonframework.modelling.command.AggregateLifecycle.apply;

@Component
@RequiredArgsConstructor
public class ProxyCommandHandler {

    private static final String PREFIX = "proxy_";

    private final TmdbService tmdbService;
    private final CommandGateway commandGateway;

//    private final EventGateway eventGateway;

    @CommandHandler
    public void handle(FetchMovieDetailsCommand command) {
        ExternalMovie externalMovie;
        try {
            externalMovie = tmdbService.searchMovie(command.getSearchPhrase());
        } catch (NotFoundInExternalServiceException e) {
            externalMovie = ExternalMovie.builder().movieState(MovieState.NOT_FOUND_IN_EXTERNAL_SERVICE).build();
        }

//        apply(new MovieDetailsFetchedEvent(command.getProxyId(), externalMovie));
        String movieId = command.getProxyId().replace(PREFIX, EMPTY);
        SaveMovieCommand saveMovieCommand = new SaveMovieCommand(movieId, externalMovie);
        commandGateway.send(saveMovieCommand);
    }
}
