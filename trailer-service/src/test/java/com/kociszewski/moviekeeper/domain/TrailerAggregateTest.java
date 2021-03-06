package com.kociszewski.moviekeeper.domain;

import com.kociszewski.moviekeeper.domain.commands.DeleteTrailersCommand;
import com.kociszewski.moviekeeper.domain.commands.CreateTrailersCommand;
import com.kociszewski.moviekeeper.domain.commands.SaveTrailersCommand;
import com.kociszewski.moviekeeper.domain.events.TrailersDeletedEvent;
import com.kociszewski.moviekeeper.domain.events.TrailersSavedEvent;
import com.kociszewski.moviekeeper.domain.events.TrailersCreatedEvent;
import com.kociszewski.moviekeeper.infrastructure.TrailerDTO;
import com.kociszewski.moviekeeper.infrastructure.TrailerSectionDTO;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.axonframework.test.aggregate.FixtureConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

public class TrailerAggregateTest {

    private FixtureConfiguration<TrailerAggregate> fixture;
    private TrailerSectionDTO trailerSectionDTO;
    private String movieId;
    private String externalMovieId;
    private String trailersId;

    @BeforeEach
    public void setup() {
        this.fixture = new AggregateTestFixture<>(TrailerAggregate.class);
        this.movieId = UUID.randomUUID().toString();
        this.trailersId = UUID.randomUUID().toString();
        this.externalMovieId = "123";

        this.trailerSectionDTO = TrailerSectionDTO.builder()
                .aggregateId(trailersId)
                .movieId(movieId)
                .externalMovieId(externalMovieId)
                .trailers(Collections.singletonList(TrailerDTO.builder()
                        .language("en")
                        .country("US")
                        .key("qazwsx")
                        .name("some trailer")
                        .site("YouTube")
                        .size(1080)
                        .type("Teaser")
                        .build()))
                .build();
    }

    @Test
    public void shouldTrailersSearchDelegatedEventEventAppear() {
        fixture.givenNoPriorActivity()
                .when(new CreateTrailersCommand(trailersId, externalMovieId, movieId))
                .expectEvents(new TrailersCreatedEvent(trailersId, movieId, externalMovieId))
                .expectState(state -> {
                    assertThat(state.getTrailersId()).isEqualTo(trailersId);
                    assertThat(state.getTrailers()).isEmpty();
                });
    }

    @Test
    public void shouldTrailersSavedEventAppearWhenTrailersEmpty() {
        fixture.given(new TrailersCreatedEvent(trailersId, movieId, externalMovieId))
                .when(new SaveTrailersCommand(trailersId, trailerSectionDTO))
                .expectEvents(new TrailersSavedEvent(trailersId, trailerSectionDTO))
                .expectState(state -> {
                    var trailers = state.getTrailers();
                    assertThat(trailers.size()).isEqualTo(1);
                    var trailer = trailers.get(0);
                    assertThat(trailer.getLanguage()).isEqualTo("en");
                    assertThat(trailer.getCountry()).isEqualTo("US");
                    assertThat(trailer.getKey()).isEqualTo("qazwsx");
                    assertThat(trailer.getName()).isEqualTo("some trailer");
                    assertThat(trailer.getSite()).isEqualTo("YouTube");
                    assertThat(trailer.getSize()).isEqualTo(1080);
                    assertThat(trailer.getType()).isEqualTo("Teaser");
                });
    }

    @Test
    public void shouldTrailersSavedEventNotAppearWhenTrailersAlreadySet() {
        fixture.given(
                new TrailersCreatedEvent(trailersId, movieId, externalMovieId),
                new TrailersSavedEvent(trailersId, trailerSectionDTO))
                .when(new SaveTrailersCommand(trailersId, trailerSectionDTO))
                .expectNoEvents()
                .expectState(state -> {
                    var trailers = state.getTrailers();
                    assertThat(trailers.size()).isEqualTo(1);
                    var trailer = trailers.get(0);
                    assertThat(trailer.getLanguage()).isEqualTo("en");
                    assertThat(trailer.getCountry()).isEqualTo("US");
                    assertThat(trailer.getKey()).isEqualTo("qazwsx");
                    assertThat(trailer.getName()).isEqualTo("some trailer");
                    assertThat(trailer.getSite()).isEqualTo("YouTube");
                    assertThat(trailer.getSize()).isEqualTo(1080);
                    assertThat(trailer.getType()).isEqualTo("Teaser");
                });
    }

    @Test
    public void shouldTrailersDeletedEventAppear() {
        fixture.given(
                new TrailersCreatedEvent(trailersId, movieId, externalMovieId),
                new TrailersSavedEvent(trailersId, trailerSectionDTO))
                .when(new DeleteTrailersCommand(trailersId))
                .expectEvents(
                        new TrailersDeletedEvent(trailersId))
                .expectMarkedDeleted();
    }

}
