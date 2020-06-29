package com.kociszewski.moviekeeper.domain;

import com.kociszewski.moviekeeper.domain.commands.FetchMovieDetailsCommand;
import com.kociszewski.moviekeeper.domain.commands.SaveMovieCommand;
import com.kociszewski.moviekeeper.domain.events.MovieDetailsEvent;
import com.kociszewski.moviekeeper.domain.events.MovieCreatedEvent;
import com.kociszewski.moviekeeper.infrastructure.ExternalMovie;
import com.kociszewski.moviekeeper.infrastructure.ExternalMovieInfo;
import com.kociszewski.moviekeeper.infrastructure.Genre;
import com.kociszewski.moviekeeper.infrastructure.MovieState;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.axonframework.test.saga.SagaTestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Date;
import java.util.UUID;

import static com.kociszewski.moviekeeper.domain.MovieAggregateTest.TITLE;

public class MovieSagaTest {
    private SagaTestFixture<MovieSaga> fixture;
    private String movieId;
    private String proxyId;
    private String searchPhrase;
    private ExternalMovie externalMovie;
    private ExternalMovie movieThatWasNotFoundInExternalService;

    @BeforeEach
    public void setup() {
        fixture = new SagaTestFixture<>(MovieSaga.class);
        fixture.registerCommandGateway(CommandGateway.class);
        fixture.registerResource(QueryUpdateEmitter.class);
        this.movieId = UUID.randomUUID().toString();
        this.proxyId = "proxy_".concat(movieId);
        this.searchPhrase = TITLE;

        this.externalMovie = ExternalMovie.builder()
                .externalMovieId("123")
                .externalMovieInfo(ExternalMovieInfo.builder()
                        .id(UUID.randomUUID().toString())
                        .genres(Collections.singletonList(new Genre("1", "comedy")))
                        .originalLanguage("pl")
                        .originalTitle(TITLE)
                        .overview("overview")
                        .posterPath("37289")
                        .releaseDate(new Date().toString())
                        .runtime(332)
                        .title(TITLE)
                        .voteAverage(2.4)
                        .voteCount(7989)
                        .insertionDate(new Date())
                        .build())
                .build();
        movieThatWasNotFoundInExternalService = ExternalMovie.builder()
                .movieState(MovieState.NOT_FOUND_IN_EXTERNAL_SERVICE)
                .build();
    }

    @Test
    public void shouldDispatchFetchMovieDetailsCommand() {
        fixture.givenAggregate(movieId)
                .published()
                .whenAggregate(movieId)
                .publishes(new MovieCreatedEvent(movieId, searchPhrase))
                .expectActiveSagas(1)
                .expectDispatchedCommands(new FetchMovieDetailsCommand(proxyId, searchPhrase));
    }

    @Test
    public void shouldDispatchSaveCastCommand() {
        fixture.givenAggregate(movieId)
                .published(new MovieCreatedEvent(movieId, searchPhrase))
                .whenAggregate(proxyId)
                .publishes(new MovieDetailsEvent(proxyId, externalMovie))
                .expectActiveSagas(0)
                .expectDispatchedCommands(new SaveMovieCommand(movieId, externalMovie));
    }

    @Test
    public void shouldNotDispatchSaveCastCommandWhenNotFoundInExternalService() {
        fixture.givenAggregate(movieId)
                .published(new MovieCreatedEvent(movieId, searchPhrase))
                .whenAggregate(proxyId)
                .publishes(new MovieDetailsEvent(proxyId, movieThatWasNotFoundInExternalService))
                .expectActiveSagas(0)
                .expectNoDispatchedCommands();
    }
}
