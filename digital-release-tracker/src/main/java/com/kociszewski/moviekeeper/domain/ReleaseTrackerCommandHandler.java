package com.kociszewski.moviekeeper.domain;


import com.kociszewski.moviekeeper.domain.commands.CreateRefreshMoviesCommand;
import com.kociszewski.moviekeeper.domain.events.RefreshMoviesDelegatedEvent;
import com.kociszewski.moviekeeper.infrastructure.MovieDTO;
import com.kociszewski.moviekeeper.infrastructure.ReleaseTrackerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventhandling.gateway.EventGateway;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReleaseTrackerCommandHandler {

    private final EventGateway eventGateway;
    private final ReleaseTrackerRepository releaseTrackerRepository;

    @CommandHandler
    public void handle(CreateRefreshMoviesCommand command) {
        List<String> notWatchedMovies = releaseTrackerRepository.findExternalMovieIdByWatchedFalse()
                .stream()
                .map(MovieDTO::getExternalMovieId)
                .collect(Collectors.toList());
        log.info("Refreshing not watched movies: " + notWatchedMovies);

        eventGateway.publish(new RefreshMoviesDelegatedEvent(command.getRefreshId(), notWatchedMovies));
    }
}
