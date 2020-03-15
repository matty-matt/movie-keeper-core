package com.kociszewski.moviekeepercore.domain.movie;


import com.kociszewski.moviekeepercore.domain.cast.events.CastDeletedEvent;
import com.kociszewski.moviekeepercore.domain.movie.commands.DeleteMovieCommand;
import com.kociszewski.moviekeepercore.domain.movie.commands.FindMovieCommand;
import com.kociszewski.moviekeepercore.domain.movie.commands.SaveMovieCommand;
import com.kociszewski.moviekeepercore.domain.movie.commands.ToggleWatchedCommand;
import com.kociszewski.moviekeepercore.domain.movie.events.MovieDeletedEvent;
import com.kociszewski.moviekeepercore.domain.movie.events.MovieSavedEvent;
import com.kociszewski.moviekeepercore.domain.movie.events.MovieSearchDelegatedEvent;
import com.kociszewski.moviekeepercore.domain.movie.events.ToggleWatchedEvent;
import com.kociszewski.moviekeepercore.domain.movie.info.Runtime;
import com.kociszewski.moviekeepercore.domain.movie.info.*;
import com.kociszewski.moviekeepercore.domain.trailer.events.TrailersDeletedEvent;
import com.kociszewski.moviekeepercore.shared.model.*;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.axonframework.test.aggregate.FixtureConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Date;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

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
    public void shouldMovieSearchDelegatedEventAppear() {
        fixture.givenNoPriorActivity()
                .when(new FindMovieCommand(movieId, trailerEntityId, castEntityId, searchPhrase))
                .expectEvents(new MovieSearchDelegatedEvent(movieId, trailerEntityId, castEntityId, searchPhrase))
                .expectState(state -> {
                    assertThat(state.getMovieId()).isEqualTo(movieId);
                    assertThat(state.getTrailerEntity().getTrailerEntityId()).isEqualTo(trailerEntityId);
                    assertThat(state.getCastEntity().getCastEntityId()).isEqualTo(castEntityId);
                    assertThat(state.getSearchPhrase()).isEqualTo(searchPhrase);
                });
    }

    @Test
    public void shouldMovieSavedEventAppear() {
        fixture.given(
                new MovieSearchDelegatedEvent(movieId, trailerEntityId, castEntityId, searchPhrase))
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
                new MovieSearchDelegatedEvent(movieId, trailerEntityId, castEntityId, searchPhrase),
                new MovieSavedEvent(movieId, externalMovie))
                .when(new ToggleWatchedCommand(movieId, new Watched(true)))
                .expectEvents(new ToggleWatchedEvent(movieId, new Watched(true)))
                .expectState(state -> assertThat(state.getWatched().isWatched()).isTrue());
    }

    @Test
    public void shouldNotToggleWatchedEventAppear() {
        fixture.given(
                new MovieSearchDelegatedEvent(movieId, trailerEntityId, castEntityId, searchPhrase),
                new MovieSavedEvent(movieId, externalMovie),
                new ToggleWatchedEvent(movieId, new Watched(true)))
                .when(new ToggleWatchedCommand(movieId, new Watched(true)))
                .expectNoEvents();
    }

    @Test
    public void shouldMovieDeletedEventAppear() {
        fixture.given(
                new MovieSearchDelegatedEvent(movieId, trailerEntityId, castEntityId, searchPhrase),
                new MovieSavedEvent(movieId, externalMovie))
                .when(new DeleteMovieCommand(movieId))
                .expectEvents(
                        new MovieDeletedEvent(movieId),
                        new TrailersDeletedEvent(trailerEntityId),
                        new CastDeletedEvent(castEntityId))
                .expectMarkedDeleted();
    }
}

