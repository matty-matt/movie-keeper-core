package com.kociszewski.moviekeepercore.domain.movie;


import com.kociszewski.moviekeepercore.domain.movie.commands.FindMovieCommand;
import com.kociszewski.moviekeepercore.domain.movie.commands.SaveMovieCommand;
import com.kociszewski.moviekeepercore.domain.movie.commands.ToggleWatchedCommand;
import com.kociszewski.moviekeepercore.domain.movie.events.MovieSavedEvent;
import com.kociszewski.moviekeepercore.domain.movie.events.MovieSearchDelegatedEvent;
import com.kociszewski.moviekeepercore.domain.movie.events.ToggleWatchedEvent;
import com.kociszewski.moviekeepercore.shared.model.*;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.axonframework.test.aggregate.FixtureConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Date;
import java.util.UUID;

public class MovieAggregateTest {
    private static final String TITLE = "some title";
    private FixtureConfiguration<MovieAggregate> fixture;
    private MovieId movieId;
    private TrailerEntityId trailerEntityId;
    private CastEntityId castEntityId;
    private SearchPhrase searchPhrase;
    private ExternalMovie externalMovie;

    @BeforeEach
    public void setup() {
        this.fixture = new AggregateTestFixture<>(MovieAggregate.class);
        this.movieId = new MovieId(UUID.randomUUID().toString());
        this.trailerEntityId = new TrailerEntityId(UUID.randomUUID().toString());
        this.castEntityId = new CastEntityId(UUID.randomUUID().toString());
        this.searchPhrase = new SearchPhrase(TITLE);
        this.externalMovie = ExternalMovie.builder()
                .externalMovieId(ExternalMovieId.builder().id("123").build())
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
                        .lastRefreshDate(new Date())
                        .build())
                .build();
    }

    @Test
    public void shouldEmitMovieSearchDelegatedEvent() {
        fixture.givenNoPriorActivity()
                .when(new FindMovieCommand(
                        movieId,
                        trailerEntityId,
                        castEntityId,
                        searchPhrase))
                .expectEvents(new MovieSearchDelegatedEvent(movieId,
                        trailerEntityId,
                        castEntityId,
                        searchPhrase));
    }

    @Test
    public void shouldEmitMovieSavedEvent() {
        fixture.given(new MovieSearchDelegatedEvent(movieId,
                trailerEntityId,
                castEntityId,
                searchPhrase))
                .when(new SaveMovieCommand(movieId, externalMovie))
                .expectEvents(new MovieSavedEvent(movieId, externalMovie));
    }

    @Test
    public void shouldEmitToggleWatchedEvent() {
        fixture.given(new MovieSearchDelegatedEvent(movieId,
                        trailerEntityId,
                        castEntityId,
                        searchPhrase),
                new MovieSavedEvent(movieId,
                        externalMovie))
                .when(new ToggleWatchedCommand(movieId, new Watched(true)))
                .expectEvents(new ToggleWatchedEvent(movieId, new Watched(true)));
    }
}

