package com.kociszewski.moviekeeper.integration;

import com.kociszewski.moviekeeper.domain.commands.FetchMovieDetailsCommand;
import com.kociszewski.moviekeeper.domain.events.MovieDetailsFetchedEvent;
import com.kociszewski.moviekeeper.infrastructure.ExternalMovie;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventhandling.gateway.EventGateway;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import static com.kociszewski.moviekeeper.integration.CommonIntegrationSetup.SUPER_MOVIE;

@Profile("test")
@Slf4j
@Component
@RequiredArgsConstructor
public class MockProxyCommandHandler {

    private final EventGateway eventGateway;

    @CommandHandler
    public void handle(FetchMovieDetailsCommand command) {
        log.info("MOCK fetching...");
        ExternalMovie movie = command.getSearchPhrase().equals(SUPER_MOVIE) ?
                CommonIntegrationSetup.MOVIE :
                CommonIntegrationSetup.ANOTHER_MOVIE;
        eventGateway.publish(new MovieDetailsFetchedEvent(command.getProxyId(), movie));
    }
}
