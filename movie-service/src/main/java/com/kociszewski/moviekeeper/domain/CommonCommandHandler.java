package com.kociszewski.moviekeeper.domain;

import com.kociszewski.moviekeeper.domain.commands.RefreshMultipleMoviesCommand;
import com.kociszewski.moviekeeper.domain.events.MultipleMoviesRefreshedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventhandling.gateway.EventGateway;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CommonCommandHandler {
    private final EventGateway eventGateway;

    @CommandHandler
    public void handle(RefreshMultipleMoviesCommand command) {
        eventGateway.publish(new MultipleMoviesRefreshedEvent(command.getRefreshedMovies()));
    }
}
