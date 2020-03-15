package com.kociszewski.moviekeepercore.domain.cast;

import com.kociszewski.moviekeepercore.domain.cast.commands.SaveCastCommand;
import com.kociszewski.moviekeepercore.domain.cast.events.CastSavedEvent;
import com.kociszewski.moviekeepercore.domain.movie.MovieAggregate;
import com.kociszewski.moviekeepercore.domain.movie.events.MovieSearchDelegatedEvent;
import com.kociszewski.moviekeepercore.infrastructure.cast.CastDTO;
import com.kociszewski.moviekeepercore.infrastructure.cast.CastInfoDTO;
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

public class CastEntityTest {

    private FixtureConfiguration<MovieAggregate> fixture;
    private MovieId movieId;
    private TrailerEntityId trailerEntityId;
    private CastEntityId castEntityId;
    private SearchPhrase searchPhrase;
    private CastDTO castDTO;

    @BeforeEach
    public void setup() {
        this.fixture = new AggregateTestFixture<>(MovieAggregate.class);
        this.movieId = new MovieId(UUID.randomUUID().toString());
        this.castDTO = CastDTO.builder()
                .aggregateId(UUID.randomUUID().toString())
                .cast(Collections.singletonList(CastInfoDTO.builder()
                        .id("678")
                        .castId("568")
                        .character("John")
                        .name("Elon Musk")
                        .gender((short)2)
                        .creditId("5c35c6b8c3a36827291bacc0")
                        .order(1)
                        .profilePath("/elon.jpg")
                        .build()))
                .movieId(movieId.getId())
                .externalMovieId("123")
                .build();

        this.trailerEntityId = new TrailerEntityId(UUID.randomUUID().toString());
        this.castEntityId = new CastEntityId(UUID.randomUUID().toString());
        this.searchPhrase = new SearchPhrase("some title");
    }

    @Test
    public void shouldCastSavedEventAppear() {
        fixture.given(
                new MovieSearchDelegatedEvent(movieId, trailerEntityId, castEntityId, searchPhrase))
                .when(new SaveCastCommand(movieId, castDTO))
                .expectEvents(new CastSavedEvent(castDTO))
                .expectState(state -> {
                    var cast = state.getCastEntity().getCast();
                    assertThat(cast.size()).isEqualTo(1);
                    var actor = cast.get(0);
                    assertThat(actor.getCastId()).isEqualTo("568");
                    assertThat(actor.getId()).isEqualTo("678");
                    assertThat(actor.getCharacter()).isEqualTo("John");
                    assertThat(actor.getName()).isEqualTo("Elon Musk");
                    assertThat(actor.getGender()).isEqualTo((short)2);
                    assertThat(actor.getCreditId()).isEqualTo("5c35c6b8c3a36827291bacc0");
                    assertThat(actor.getOrder()).isEqualTo(1);
                    assertThat(actor.getProfilePath()).isEqualTo("/elon.jpg");
                });
    }
}
