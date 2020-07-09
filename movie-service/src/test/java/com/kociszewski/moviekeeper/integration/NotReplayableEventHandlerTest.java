package com.kociszewski.moviekeeper.integration;

import com.kociszewski.moviekeeper.domain.commands.RefreshMovieCommand;
import com.kociszewski.moviekeeper.domain.events.MovieDeletedEvent;
import com.kociszewski.moviekeeper.domain.events.MoviesRefreshedEvent;
import com.kociszewski.moviekeeper.domain.events.MultipleMoviesRefreshedEvent;
import com.kociszewski.moviekeeper.domain.events.TrailersAndCastSearchDelegatedEvent;
import com.kociszewski.moviekeeper.infrastructure.MovieDTO;
import com.kociszewski.moviekeeper.infrastructure.MovieProjection;
import com.kociszewski.moviekeeper.infrastructure.RefreshData;
import com.kociszewski.moviekeeper.integration.common.TestContainers;
import com.kociszewski.moviekeeper.notreplayable.NotReplayableEventHandler;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.gateway.EventGateway;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class NotReplayableEventHandlerTest {

    @Autowired
    private NotReplayableEventHandler subject;

    @MockBean
    private EventGateway eventGateway;

    @MockBean
    private CommandGateway commandGateway;

    @MockBean
    private MovieProjection movieProjection;

    private List<RefreshData> refreshDataList;
    private List<MovieDTO> refreshedMovies;
    private String firstAggregateId;
    private String movieId;
    private String trailersId;
    private String castId;

    @BeforeAll
    public static void beforeAll() {
        TestContainers.startAxonServer();
        TestContainers.startMongo();
    }

    @BeforeEach
    public void before() {
        movieId = UUID.randomUUID().toString();
        trailersId = UUID.randomUUID().toString();
        castId = UUID.randomUUID().toString();
        firstAggregateId = UUID.randomUUID().toString();
        String secondAggregateId = UUID.randomUUID().toString();
        refreshDataList = Arrays.asList(
                RefreshData.builder()
                        .aggregateId(firstAggregateId)
                        .voteCount(34)
                        .averageVote(7.5)
                        .digitalReleaseDate("2020-05-05T00:00")
                        .movieId("123")
                        .build(),
                RefreshData.builder()
                        .aggregateId(secondAggregateId)
                        .voteCount(89)
                        .averageVote(6.2)
                        .digitalReleaseDate("2020-07-06T00:00")
                        .movieId("234")
                        .build()
        );
        refreshedMovies = Arrays.asList(
           MovieDTO.builder()
                   .aggregateId(firstAggregateId)
                   .releaseDateDigital("2020-05-15T00:00")
                   .voteCount(40)
                   .voteAverageMdb(7.0)
                   .externalMovieId("123")
                   .build(),
           MovieDTO.builder()
                   .aggregateId(secondAggregateId)
                   .voteCount(100)
                   .voteAverageMdb(6.8)
                   .releaseDateDigital("2020-07-06T00:00")
                   .externalMovieId("234")
                   .build()
        );
    }

    @Test
    public void shouldHandleMultipleMoviesRefreshedEvent() {
        // given
        ArgumentCaptor<MoviesRefreshedEvent> eventCaptor = ArgumentCaptor.forClass(MoviesRefreshedEvent.class);
        ArgumentCaptor<RefreshMovieCommand> commandCaptor = ArgumentCaptor.forClass(RefreshMovieCommand.class);
        when(movieProjection.refreshMovies(refreshDataList)).thenReturn(refreshedMovies);
        doNothing().when(eventGateway).publish(eventCaptor.capture());
        when(commandGateway.send(commandCaptor.capture())).thenReturn(null);

        // when
        subject.handle(new MultipleMoviesRefreshedEvent(refreshDataList));

        // then
        verify(eventGateway, times(1)).publish(eventCaptor.getValue());
        verify(movieProjection, times(1)).refreshMovies(refreshDataList);
        verify(commandGateway, times(1)).send(commandCaptor.getAllValues().get(0));
        verify(commandGateway, times(1)).send(commandCaptor.getAllValues().get(1));
        assertThat(eventCaptor.getValue().getRefreshedMovies()).isEqualTo(refreshedMovies);
        assertThat(commandCaptor.getValue().getMovieId()).isEqualTo(firstAggregateId);
    }

    @Test
    public void shouldHandleTrailersAndCastSearchDelegatedEvent() {
        // given
        String externalMovieId = "123";
        when(commandGateway.send(any())).thenReturn(null);

        // when
        subject.handle(new TrailersAndCastSearchDelegatedEvent(movieId, externalMovieId, trailersId, castId));

        // then
        verify(commandGateway, times(2)).send(any());
    }

    @Test
    public void shouldHandleMovieDeletedEvent() {
        // given
        when(commandGateway.send(any())).thenReturn(null);

        // when
        subject.handle(new MovieDeletedEvent(movieId, trailersId, castId));

        // then
        verify(commandGateway, times(2)).send(any());
    }
}
