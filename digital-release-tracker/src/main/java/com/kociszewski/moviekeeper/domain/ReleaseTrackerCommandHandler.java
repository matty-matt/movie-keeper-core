package com.kociszewski.moviekeeper.domain;


import com.kociszewski.moviekeeper.domain.commands.CreateRefreshMoviesCommand;
import com.kociszewski.moviekeeper.domain.events.RefreshMoviesDelegatedEvent;
import com.kociszewski.moviekeeper.domain.queries.GetNotSeenMoviesQuery;
import com.kociszewski.moviekeeper.domain.queries.GetRefreshedMoviesQuery;
import com.kociszewski.moviekeeper.infrastructure.MovieDTO;
import com.kociszewski.moviekeeper.infrastructure.RefreshMovie;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventhandling.gateway.EventGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReleaseTrackerCommandHandler {

    private final EventGateway eventGateway;
    private final QueryGateway queryGateway;
    private final QueryUpdateEmitter queryUpdateEmitter;

    @CommandHandler
    public void handle(CreateRefreshMoviesCommand command) {
        List<RefreshMovie> moviesToRefresh = queryGateway.query(new GetNotSeenMoviesQuery(), ResponseTypes.multipleInstancesOf(MovieDTO.class))
                .join()
                .stream()
                .map(movie -> RefreshMovie
                        .builder()
                        .aggregateId(movie.getAggregateId())
                        .externalMovieId(movie.getExternalMovieId())
                        .build())
                .collect(Collectors.toList());
        if (moviesToRefresh.isEmpty()) {
            log.info("No movies to refresh. Database is empty!");
            queryUpdateEmitter.emit(GetRefreshedMoviesQuery.class, query -> true, Collections.emptyList());
        } else {
            eventGateway.publish(new RefreshMoviesDelegatedEvent(command.getRefreshId(), moviesToRefresh));
        }
    }
}
