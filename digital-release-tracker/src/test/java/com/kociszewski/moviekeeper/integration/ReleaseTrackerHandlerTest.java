package com.kociszewski.moviekeeper.integration;

import com.kociszewski.moviekeeper.domain.ReleaseTrackerHandler;
import com.kociszewski.moviekeeper.domain.commands.CreateRefreshMoviesCommand;
import com.kociszewski.moviekeeper.domain.events.MoviesRefreshedEvent;
import com.kociszewski.moviekeeper.domain.events.RefreshMoviesDelegatedEvent;
import com.kociszewski.moviekeeper.infrastructure.MovieDTO;
import com.kociszewski.moviekeeper.infrastructure.RefreshMovie;
import com.kociszewski.moviekeeper.integration.common.TestContainersSetup;
import org.axonframework.eventhandling.gateway.EventGateway;
import org.axonframework.messaging.responsetypes.ResponseType;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ReleaseTrackerHandlerTest extends TestContainersSetup {

    @Autowired
    private ReleaseTrackerHandler subject;

    @MockBean
    private EventGateway eventGateway;

    @MockBean
    private QueryGateway queryGateway;

    @MockBean
    private QueryUpdateEmitter queryUpdateEmitter;

    @Test
    public void shouldEmitEmptyListWhenNoMoviesToRefresh() {
        // given
        String refreshId = UUID.randomUUID().toString();
        when(queryGateway.query(any(), (ResponseType<Object>) any()))
                .thenReturn(CompletableFuture.completedFuture(Collections.emptyList()));

        // when
        subject.handle(new CreateRefreshMoviesCommand(refreshId));

        // then
        verify(eventGateway, times(0)).publish((Object) any());
        verify(queryUpdateEmitter, times(1)).emit(any(), any(Predicate.class), eq(Collections.emptyList()));
    }

    @Test
    public void shouldEmitRefreshMoviesDelegatedEventWhenMoviesArePresent() {
        // given
        String aggregateId = UUID.randomUUID().toString();
        String refreshId = UUID.randomUUID().toString();
        List<MovieDTO> notSeenMovies = Collections.singletonList(MovieDTO.builder()
                .externalMovieId("123")
                .aggregateId(aggregateId)
                .build());
        ArgumentCaptor<RefreshMoviesDelegatedEvent> captor = ArgumentCaptor.forClass(RefreshMoviesDelegatedEvent.class);

        when(queryGateway.query(any(), (ResponseType<Object>) any()))
                .thenReturn(CompletableFuture.completedFuture(notSeenMovies));
        doNothing().when(eventGateway).publish(captor.capture());

        // when
        subject.handle(new CreateRefreshMoviesCommand(refreshId));

        // then
        verify(eventGateway, times(1)).publish(captor.getValue());
        verify(queryUpdateEmitter, times(0)).emit(any(), any(Predicate.class), any(List.class));
        assertThat(captor.getValue().getMoviesToRefresh().get(0).getAggregateId()).isEqualTo(aggregateId);
        assertThat(captor.getValue().getMoviesToRefresh().get(0).getExternalMovieId()).isEqualTo("123");
    }

    @Test
    public void shouldEmitRefreshedMovies() {
        // given
        List<MovieDTO> refreshedMovies = Collections.singletonList(MovieDTO.builder().build());

        // when
        subject.handle(new MoviesRefreshedEvent(refreshedMovies));

        // then
        verify(queryUpdateEmitter, times(1)).emit(any(), any(Predicate.class), eq(refreshedMovies));

    }
}
