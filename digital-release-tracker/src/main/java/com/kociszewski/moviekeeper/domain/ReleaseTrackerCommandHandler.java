package com.kociszewski.moviekeeper.domain;


import com.kociszewski.moviekeeper.domain.commands.RefreshMoviesCommand;
import com.kociszewski.moviekeeper.domain.events.MoviesRefreshedEvent;
import com.kociszewski.moviekeeper.infrastructure.MovieDTO;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventhandling.gateway.EventGateway;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ReleaseTrackerCommandHandler {

    private final EventGateway eventGateway;

    @CommandHandler
    public void handle(RefreshMoviesCommand command) {
        eventGateway.publish(new MoviesRefreshedEvent(Arrays.asList(
                MovieDTO.builder()
                        .aggregateId(UUID.randomUUID().toString())
                        .title("Test")
                        .releaseDateDigital("2020-06-30T00:00").build(),
                MovieDTO.builder()
                        .aggregateId(UUID.randomUUID().toString())
                        .title("TEST2")
                        .releaseDateDigital("takitam")
                        .build())));
    }
}
