package com.kociszewski.moviekeepercore.domain.movie;

import com.kociszewski.moviekeepercore.domain.ExternalService;
import com.kociszewski.moviekeepercore.domain.movie.commands.SaveMovieCommand;
import com.kociszewski.moviekeepercore.domain.movie.events.MovieSearchDelegatedEvent;
import com.kociszewski.moviekeepercore.shared.model.ExternalMovie;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MovieEventsHandler {
    private final ExternalService externalService;
    private final CommandGateway commandGateway;

    @EventHandler
    public void on(MovieSearchDelegatedEvent event) {
        ExternalMovie externalMovie = externalService.searchMovie(event.getSearchPhrase(), event.getMovieId());
        commandGateway.sendAndWait(new SaveMovieCommand(event.getMovieId(), externalMovie));
    }
}
