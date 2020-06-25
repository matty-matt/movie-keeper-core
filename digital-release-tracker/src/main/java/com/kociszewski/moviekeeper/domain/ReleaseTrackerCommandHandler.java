package com.kociszewski.moviekeeper.domain;


import com.kociszewski.moviekeeper.domain.commands.CreateRefreshMoviesCommand;
import com.kociszewski.moviekeeper.domain.commands.SaveRefreshedMoviesCommand;
import com.kociszewski.moviekeeper.domain.events.MoviesRefreshedEvent;
import com.kociszewski.moviekeeper.domain.events.RefreshMoviesDelegatedEvent;
import com.kociszewski.moviekeeper.infrastructure.ReleaseTrackerProjection;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventhandling.gateway.EventGateway;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReleaseTrackerCommandHandler {

    private final EventGateway eventGateway;
    private final ReleaseTrackerProjection releaseTrackerProjection;

    @CommandHandler
    public void handle(CreateRefreshMoviesCommand command) {
        eventGateway.publish(new RefreshMoviesDelegatedEvent(command.getRefreshId(), releaseTrackerProjection.findMoviesToRefresh()));
    }

    @CommandHandler
    public void handle(SaveRefreshedMoviesCommand command) {
        eventGateway.publish(new MoviesRefreshedEvent(command.getRefreshedMovies()));
    }
}
