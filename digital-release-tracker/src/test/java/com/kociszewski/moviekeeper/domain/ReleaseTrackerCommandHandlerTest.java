package com.kociszewski.moviekeeper.domain;

import com.kociszewski.moviekeeper.domain.commands.CreateRefreshMoviesCommand;
import com.kociszewski.moviekeeper.domain.commands.SaveRefreshedMoviesCommand;
import com.kociszewski.moviekeeper.domain.events.MoviesRefreshedEvent;
import com.kociszewski.moviekeeper.domain.events.RefreshMoviesDelegatedEvent;
import com.kociszewski.moviekeeper.infrastructure.RefreshData;
import com.kociszewski.moviekeeper.infrastructure.RefreshMovie;
import com.kociszewski.moviekeeper.infrastructure.ReleaseTrackerProjection;
import org.axonframework.eventhandling.gateway.EventGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReleaseTrackerCommandHandlerTest {

    @Autowired
    private ReleaseTrackerCommandHandler subject;

    @MockBean
    private EventGateway eventGateway;

    @MockBean
    private ReleaseTrackerProjection releaseTrackerProjection;

    @Test
    public void shouldNotEmitEventWhenNoMoviesToRefresh() {
        // given
        String refreshId = UUID.randomUUID().toString();
        when(releaseTrackerProjection.findMoviesToRefresh()).thenReturn(Collections.emptyList());

        // when
        subject.handle(new CreateRefreshMoviesCommand(refreshId));

        // then
        verify(eventGateway, times(0)).publish((Object) any());
    }

    @Test
    public void shouldEmitRefreshEventWhenMoviesArePresent() {
        // given
        List<RefreshMovie> refreshedMovies = Collections.singletonList(RefreshMovie.builder().build());
        String refreshId = UUID.randomUUID().toString();
        when(releaseTrackerProjection.findMoviesToRefresh()).thenReturn(refreshedMovies);
        doNothing().when(eventGateway).publish(new RefreshMoviesDelegatedEvent(refreshId, refreshedMovies));

        // when
        subject.handle(new CreateRefreshMoviesCommand(refreshId));

        // then
        verify(eventGateway, times(1)).publish(new RefreshMoviesDelegatedEvent(refreshId, refreshedMovies));
    }

    @Test
    public void shouldEmitMoviesRefreshedEvent() {
        // given
        List<RefreshData> refreshedData = Collections.singletonList(RefreshData.builder().build());
        String refreshId = UUID.randomUUID().toString();
        doNothing().when(eventGateway).publish(new MoviesRefreshedEvent(refreshedData));

        // when
        subject.handle(new SaveRefreshedMoviesCommand(refreshId, refreshedData));

        // then
        verify(eventGateway, times(1)).publish(new MoviesRefreshedEvent(refreshedData));
    }
}
