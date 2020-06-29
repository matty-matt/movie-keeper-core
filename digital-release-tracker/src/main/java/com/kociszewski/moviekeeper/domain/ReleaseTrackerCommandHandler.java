package com.kociszewski.moviekeeper.domain;


import com.kociszewski.moviekeeper.domain.commands.CreateRefreshMoviesCommand;
import com.kociszewski.moviekeeper.domain.commands.SaveRefreshedMoviesCommand;
import com.kociszewski.moviekeeper.domain.events.MoviesRefreshedEvent;
import com.kociszewski.moviekeeper.domain.events.RefreshMoviesDelegatedEvent;
import com.kociszewski.moviekeeper.infrastructure.ReleaseTrackerProjection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventhandling.gateway.EventGateway;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReleaseTrackerCommandHandler {

    private final EventGateway eventGateway;
    private final ReleaseTrackerProjection releaseTrackerProjection;

    @CommandHandler
    public void handle(CreateRefreshMoviesCommand command) {
        List<String> moviesToRefresh = releaseTrackerProjection.findMoviesToRefresh();
        if (moviesToRefresh.isEmpty()) {
            log.info("No movies to refresh. Database is empty!");
        } else {
            eventGateway.publish(new RefreshMoviesDelegatedEvent(command.getRefreshId(), moviesToRefresh));
        }
    }

    @CommandHandler
    public void handle(SaveRefreshedMoviesCommand command) {
        eventGateway.publish(new MoviesRefreshedEvent(command.getRefreshedMovies()));
    }
}
