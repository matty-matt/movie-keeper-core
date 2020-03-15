package com.kociszewski.moviekeepercore.domain.trailer;

import com.kociszewski.moviekeepercore.domain.movie.MovieAggregate;
import com.kociszewski.moviekeepercore.domain.movie.events.MovieSearchDelegatedEvent;
import com.kociszewski.moviekeepercore.domain.trailer.commands.SaveTrailersCommand;
import com.kociszewski.moviekeepercore.domain.trailer.events.TrailersSavedEvent;
import com.kociszewski.moviekeepercore.infrastructure.trailer.TrailerDTO;
import com.kociszewski.moviekeepercore.infrastructure.trailer.TrailerSectionDTO;
import com.kociszewski.moviekeepercore.shared.model.CastEntityId;
import com.kociszewski.moviekeepercore.shared.model.MovieId;
import com.kociszewski.moviekeepercore.shared.model.SearchPhrase;
import com.kociszewski.moviekeepercore.shared.model.TrailerEntityId;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.axonframework.test.aggregate.FixtureConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class TrailerEntityTest {

    private FixtureConfiguration<MovieAggregate> fixture;
    private MovieId movieId;
    private TrailerSectionDTO trailerSectionDTO;
    private TrailerEntityId trailerEntityId;
    private CastEntityId castEntityId;
    private SearchPhrase searchPhrase;

    @BeforeEach
    public void setup() {
        this.fixture = new AggregateTestFixture<>(MovieAggregate.class);
        this.movieId = new MovieId(UUID.randomUUID().toString());
        this.trailerSectionDTO = TrailerSectionDTO.builder()
                .aggregateId(UUID.randomUUID().toString())
                .movieId(movieId.getId())
                .externalMovieId("123")
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

        this.trailerEntityId = new TrailerEntityId(UUID.randomUUID().toString());
        this.castEntityId = new CastEntityId(UUID.randomUUID().toString());
        this.searchPhrase = new SearchPhrase("some title");
    }

    @Test
    public void shouldTrailersSavedEventAppear() {
        fixture.given(
                new MovieSearchDelegatedEvent(movieId, trailerEntityId, castEntityId, searchPhrase))
                .when(new SaveTrailersCommand(movieId, trailerSectionDTO))
                .expectEvents(new TrailersSavedEvent(trailerSectionDTO))
                .expectState(state -> {
                    var trailers = state.getTrailerEntity().getTrailers();
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

}
