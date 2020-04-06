package com.kociszewski.moviekeeper.domain;

import com.kociszewski.moviekeeper.domain.commands.FetchCastCommand;
import com.kociszewski.moviekeeper.domain.events.CastSearchDelegatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;

import static org.axonframework.modelling.saga.SagaLifecycle.associateWith;

@Saga
@Slf4j
public class CastSaga {

    private static final String PROXY_PREFIX = "proxy_";

    @Autowired
    private CommandGateway commandGateway;

    @StartSaga
    @SagaEventHandler(associationProperty = "castId")
    public void handle(CastSearchDelegatedEvent event) {
        log.info("[saga] Handling {}, id={}", event.getClass().getSimpleName(), event.getCastId());
        String proxyId = PROXY_PREFIX.concat(event.getMovieId());
        log.info("[saga] This is proxyId={}", proxyId);
        associateWith("proxyId", proxyId);
        commandGateway.send(new FetchCastCommand(proxyId, event.getExternalMovieId(), event.getCastId()));
    }
}
