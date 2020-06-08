package com.kociszewski.moviekeeper.domain;

import com.kociszewski.moviekeeper.domain.commands.FetchCastCommand;
import com.kociszewski.moviekeeper.domain.commands.SaveCastCommand;
import com.kociszewski.moviekeeper.domain.events.CastDetailsEvent;
import com.kociszewski.moviekeeper.domain.events.CastCreatedEvent;
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
    public void handle(CastCreatedEvent event) {
        log.info("[saga] Handling {}, castId={}, movieId={}, externalId={}",
                event.getClass().getSimpleName(),
                event.getCastId(),
                event.getMovieId(),
                event.getExternalMovieId());
        castId = event.getCastId();
        String proxyId = PROXY_PREFIX.concat(event.getMovieId());
        associateWith("proxyId", proxyId);
        commandGateway.send(new FetchCastCommand(proxyId, event.getExternalMovieId(), event.getCastId()));
    }

    @SagaEventHandler(associationProperty = "proxyId")
    @EndSaga
    public void handle(CastDetailsEvent event) {
        log.info("[saga] Handling {}, id={}", event.getClass().getSimpleName(), event.getProxyId());

        var cast = event.getCastDTO();
        commandGateway.send(new SaveCastCommand(castId, cast));
    }
}
