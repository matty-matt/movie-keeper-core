package com.kociszewski.moviekeeper.domain;

import com.kociszewski.moviekeeper.domain.commands.FetchTrailersCommand;
import com.kociszewski.moviekeeper.domain.commands.SaveTrailersCommand;
import com.kociszewski.moviekeeper.domain.events.TrailersDetailsEvent;
import com.kociszewski.moviekeeper.domain.events.TrailersCreatedEvent;
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
public class TrailerSaga {

    private static final String PROXY_PREFIX = "proxy_";

    @Autowired
    private CommandGateway commandGateway;

    private String trailersId;

    @StartSaga
    @SagaEventHandler(associationProperty = "trailersId")
    public void handle(TrailersCreatedEvent event) {
        log.info("[saga] Handling {}, trailersId={}, movieId={}, externalId={}",
                event.getClass().getSimpleName(),
                event.getTrailersId(),
                event.getMovieId(),
                event.getExternalMovieId());
        trailersId = event.getTrailersId();
        String proxyId = PROXY_PREFIX.concat(event.getMovieId());
        associateWith("proxyId", proxyId);
        commandGateway.send(new FetchTrailersCommand(proxyId, event.getExternalMovieId(), event.getTrailersId()));
    }

    @SagaEventHandler(associationProperty = "proxyId")
    @EndSaga
    public void handle(TrailersDetailsEvent event) {
        log.info("[saga] Handling {}, id={}", event.getClass().getSimpleName(), event.getProxyId());

        var trailers = event.getTrailerSectionDTO();
        commandGateway.send(new SaveTrailersCommand(trailersId, trailers));
    }
}
