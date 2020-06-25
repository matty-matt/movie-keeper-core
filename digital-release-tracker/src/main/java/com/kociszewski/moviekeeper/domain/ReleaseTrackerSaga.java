package com.kociszewski.moviekeeper.domain;

import com.kociszewski.moviekeeper.domain.commands.RefreshMoviesCommand;
import com.kociszewski.moviekeeper.domain.events.RefreshMoviesDelegatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;

import static org.axonframework.modelling.saga.SagaLifecycle.associateWith;

@Saga
@Slf4j
public class ReleaseTrackerSaga {

    private static final String REFRESH_ID = "refreshId";
    private static final String PROXY_PREFIX = "proxy_";

    @Autowired
    private CommandGateway commandGateway;

    @StartSaga
    @SagaEventHandler(associationProperty = REFRESH_ID)
    public void handle(RefreshMoviesDelegatedEvent event) {
        log.info("[saga] Handling {}, id={}", event.getClass().getSimpleName(), event.getRefreshId());
        String proxyId = PROXY_PREFIX.concat(event.getRefreshId());
        associateWith("proxyId", proxyId);
        commandGateway.send(new RefreshMoviesCommand(proxyId, event.getMoviesToRefresh()));
    }
}
