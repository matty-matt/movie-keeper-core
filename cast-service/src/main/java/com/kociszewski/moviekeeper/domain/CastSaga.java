package com.kociszewski.moviekeeper.domain;

import com.kociszewski.moviekeeper.domain.commands.FetchCastDetailsCommand;
import com.kociszewski.moviekeeper.domain.commands.SaveCastCommand;
import com.kociszewski.moviekeeper.domain.events.CastDetailsFetchedEvent;
import com.kociszewski.moviekeeper.domain.events.CastSearchDelegatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.modelling.saga.EndSaga;
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

    private String castId;

    @StartSaga
    @SagaEventHandler(associationProperty = "castId")
    public void handle(CastSearchDelegatedEvent event) {
        log.info("[saga] Handling {}, id={}", event.getClass().getSimpleName(), event.getCastId());
        castId = event.getCastId();
        String proxyId = PROXY_PREFIX.concat(event.getMovieId());
        associateWith("proxyId", proxyId);
        commandGateway.send(new FetchCastDetailsCommand(proxyId, event.getExternalMovieId(), event.getCastId()));
    }

    @SagaEventHandler(associationProperty = "proxyId")
    @EndSaga
    public void handle(CastDetailsFetchedEvent event) {
        log.info("[saga] Handling {}, id={}", event.getClass().getSimpleName(), event.getProxyId());

        var cast = event.getCastDTO();
        commandGateway.send(new SaveCastCommand(castId, cast));
    }
}
