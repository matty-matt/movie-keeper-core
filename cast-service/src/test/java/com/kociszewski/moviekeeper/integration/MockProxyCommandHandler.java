package com.kociszewski.moviekeeper.integration;

import com.kociszewski.moviekeeper.domain.commands.FetchCastDetailsCommand;
import com.kociszewski.moviekeeper.domain.events.CastDetailsFetchedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventhandling.gateway.EventGateway;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import static com.kociszewski.moviekeeper.integration.CommonIntegrationSetup.CAST;

@Profile("test")
@Slf4j
@Component
@RequiredArgsConstructor
public class MockProxyCommandHandler {

    private final EventGateway eventGateway;

    @CommandHandler
    public void handle(FetchCastDetailsCommand command) {
        log.info("MOCK fetching...");
        eventGateway.publish(new CastDetailsFetchedEvent(command.getProxyId(), CAST));
    }
}
