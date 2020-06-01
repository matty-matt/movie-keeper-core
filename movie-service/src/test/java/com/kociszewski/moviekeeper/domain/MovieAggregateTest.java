package com.kociszewski.moviekeeper.domain;


import com.kociszewski.moviekeeper.domain.commands.DeleteMovieCommand;
import com.kociszewski.moviekeeper.domain.commands.FindMovieCommand;
import com.kociszewski.moviekeeper.domain.commands.SaveMovieCommand;
import com.kociszewski.moviekeeper.domain.commands.ToggleWatchedCommand;
import com.kociszewski.moviekeeper.domain.events.MovieDeletedEvent;
import com.kociszewski.moviekeeper.domain.events.MovieSavedEvent;
import com.kociszewski.moviekeeper.domain.events.MovieSearchDelegatedEvent;
import com.kociszewski.moviekeeper.domain.events.ToggleWatchedEvent;
import com.kociszewski.moviekeeper.domain.info.*;
import com.kociszewski.moviekeeper.domain.info.Runtime;
import com.kociszewski.moviekeeper.infrastructure.*;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.axonframework.test.aggregate.FixtureConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Date;
import java.util.UUID;

import static org.assertj.core.api.Assertions.anyOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

public class MovieAggregateTest {
    public static final String TITLE = "some title";
    private FixtureConfiguration<MovieAggregate> fixture;
    private String movieId;
    private String externalMovieId;
    private String searchPhrase;
    private ExternalMovie externalMovie;

    @BeforeEach
    public void setup() {
        this.fixture = new AggregateTestFixture<>(MovieAggregate.class);
        this.movieId = UUID.randomUUID().toString();
        this.externalMovieId = "123";
        this.searchPhrase = TITLE;
        this.externalMovie = ExternalMovie.builder()
                .externalMovieId(externalMovieId)
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
    public void shouldMovieSearchDelegatedEventAppear() {
        fixture.givenNoPriorActivity()
                .when(new FindMovieCommand(movieId, searchPhrase))
                .expectEvents(new MovieSearchDelegatedEvent(movieId, searchPhrase))
                .expectState(state -> {
                    assertThat(state.getMovieId()).isEqualTo(movieId);
                    assertThat(state.getSearchPhrase()).isEqualTo(new SearchPhrase(searchPhrase));
                });
    }

    @Test
    public void shouldMovieSavedEventAppear() {
        fixture.given(
                new MovieSearchDelegatedEvent(movieId, searchPhrase))
                .when(new SaveMovieCommand(movieId, externalMovie))
                .expectEvents(new MovieSavedEvent(movieId, externalMovie))
                .expectState(state -> {
                    assertThat(state.getExternalMovieId()).isEqualTo(externalMovie.getExternalMovieId());
                    assertThat(state.getPoster()).isEqualTo(new Poster(externalMovie.getExternalMovieInfo().getPosterPath()));
                    assertThat(state.getTitle()).isEqualTo(new Title(externalMovie.getExternalMovieInfo().getTitle()));
                    assertThat(state.getOriginalTitle()).isEqualTo(new Title(externalMovie.getExternalMovieInfo().getOriginalTitle()));
                    assertThat(state.getOverview()).isEqualTo(new Overview(externalMovie.getExternalMovieInfo().getOverview()));
                    assertThat(state.getDigitalRelease()).isEqualTo(new Release(externalMovie.getDigitalRelease()));
                    assertThat(state.getPremiereRelease()).isEqualTo(new Release(externalMovie.getExternalMovieInfo().getReleaseDate()));
                    assertThat(state.getVote()).isEqualTo(
                            new Vote(externalMovie.getExternalMovieInfo().getVoteAverage(), externalMovie.getExternalMovieInfo().getVoteCount()));
                    assertThat(state.getRuntime()).isEqualTo(new Runtime(externalMovie.getExternalMovieInfo().getRuntime()));
                    assertThat(state.getInsertionDate()).isEqualTo(new DateWrapper(externalMovie.getExternalMovieInfo().getInsertionDate()));
                    assertThat(state.getLastRefreshDate()).isEqualTo(new DateWrapper(externalMovie.getExternalMovieInfo().getLastRefreshDate()));
                    assertThat(state.getWatched()).isEqualTo(new Watched(false));
                    assertThat(state.getOriginalLanguage()).isEqualTo(new Language(externalMovie.getExternalMovieInfo().getOriginalLanguage()));
                    assertThat(state.getGenres()).isEqualTo(externalMovie.getExternalMovieInfo().getGenres());
                });
    }

    @Test
    public void shouldToggleWatchedEventAppear() {
        fixture.given(
                new MovieSearchDelegatedEvent(movieId, searchPhrase),
                new MovieSavedEvent(movieId, externalMovie))
                .when(new ToggleWatchedCommand(movieId, new Watched(true)))
                .expectEvents(new ToggleWatchedEvent(movieId, new Watched(true)))
                .expectState(state -> assertThat(state.getWatched().isWatched()).isTrue());
    }

    @Test
    public void shouldNotToggleWatchedEventAppear() {
        fixture.given(
                new MovieSearchDelegatedEvent(movieId, searchPhrase),
                new MovieSavedEvent(movieId, externalMovie),
                new ToggleWatchedEvent(movieId, new Watched(true)))
                .when(new ToggleWatchedCommand(movieId, new Watched(true)))
                .expectNoEvents();
    }

    @Test
    public void shouldMovieDeletedEventAppear() {
        fixture.given(
                new MovieSearchDelegatedEvent(movieId, searchPhrase),
                new MovieSavedEvent(movieId, externalMovie))
                .when(new DeleteMovieCommand(movieId))
                .expectEvents(
                        new MovieDeletedEvent(movieId, any(), any()))
                .expectMarkedDeleted();
    }
}

