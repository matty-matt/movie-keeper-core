package com.kociszewski.moviekeeper.domain;

import com.kociszewski.moviekeeper.domain.commands.RefreshMoviesCommand;
import com.kociszewski.moviekeeper.domain.commands.SaveRefreshedMoviesCommand;
import com.kociszewski.moviekeeper.domain.commands.UpdateRefreshDataCommand;
import com.kociszewski.moviekeeper.domain.events.MoviesRefreshDataEvent;
import com.kociszewski.moviekeeper.domain.events.RefreshMoviesDelegatedEvent;
import com.kociszewski.moviekeeper.infrastructure.RefreshData;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.axonframework.modelling.saga.SagaLifecycle.associateWith;
import static org.axonframework.modelling.saga.SagaLifecycle.end;

@Saga
@Slf4j
public class ReleaseTrackerSaga {

    private static final String REFRESH_ID = "refreshId";
    private static final String PROXY_PREFIX = "proxy_";
    private static final String PROXY_ID = "proxyId";

    private String refreshId;

    @Autowired
    private CommandGateway commandGateway;

    @StartSaga
    @SagaEventHandler(associationProperty = REFRESH_ID)
    public void handle(RefreshMoviesDelegatedEvent event) {
        log.info("[saga] Handling {}, id={}", event.getClass().getSimpleName(), event.getRefreshId());
        refreshId = event.getRefreshId();
        String proxyId = PROXY_PREFIX.concat(refreshId);
        associateWith("proxyId", proxyId);
        commandGateway.send(new RefreshMoviesCommand(proxyId, event.getMoviesToRefresh()));
    }

    @EndSaga
    @SagaEventHandler(associationProperty = PROXY_ID)
    public void handle(MoviesRefreshDataEvent event) {
        log.info("[saga] Handling {}, id={}", event.getClass().getSimpleName(), event.getProxyId());
        String proxyId = PROXY_PREFIX.concat(event.getProxyId());
        associateWith("proxyId", proxyId);
        commandGateway.send(new SaveRefreshedMoviesCommand(refreshId, event.getRefreshedMovies()));
        delegateAggregateUpdates(event.getRefreshedMovies());
        end();
    }

    private void delegateAggregateUpdates(List<RefreshData> refreshedMovies) {
        refreshedMovies.parallelStream()
                .forEach(movie -> commandGateway.send(new UpdateRefreshDataCommand(movie.getAggregateId(), movie)));
    }
}
