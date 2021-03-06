package com.kociszewski.moviekeeper.domain;

import com.kociszewski.moviekeeper.domain.commands.DeleteCastCommand;
import com.kociszewski.moviekeeper.domain.commands.CreateCastCommand;
import com.kociszewski.moviekeeper.domain.commands.SaveCastCommand;
import com.kociszewski.moviekeeper.domain.events.CastDeletedEvent;
import com.kociszewski.moviekeeper.domain.events.CastSavedEvent;
import com.kociszewski.moviekeeper.domain.events.CastCreatedEvent;
import com.kociszewski.moviekeeper.infrastructure.CastDTO;
import com.kociszewski.moviekeeper.infrastructure.CastInfoDTO;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.axonframework.test.aggregate.FixtureConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class CastAggregateTest {

    private FixtureConfiguration<CastAggregate> fixture;
    private CastDTO castDTO;
    private String movieId;
    private String externalMovieId;
    private String castId;

    @BeforeEach
    public void setup() {
        this.fixture = new AggregateTestFixture<>(CastAggregate.class);
        this.movieId = UUID.randomUUID().toString();
        this.castId = UUID.randomUUID().toString();
        this.externalMovieId = "123";

        this.castDTO = CastDTO.builder()
                .aggregateId(castId)
                .movieId(movieId)
                .externalMovieId(externalMovieId)
                .cast(Collections.singletonList(CastInfoDTO.builder()
                        .id("678")
                        .castId("568")
                        .character("John")
                        .name("Elon Musk")
                        .gender((short)2)
                        .order(1)
                        .profilePath("/elon.jpg")
                        .build()))
                .build();
    }

    @Test
    public void shouldCastSearchDelegatedEventAppear() {
        fixture.givenNoPriorActivity()
                .when(new CreateCastCommand(castId, externalMovieId, movieId))
                .expectEvents(new CastCreatedEvent(castId, movieId, externalMovieId))
                .expectState(state -> {
                    assertThat(state.getCastId()).isEqualTo(castId);
                    assertThat(state.getCast()).isEmpty();
                });
    }

    @Test
    public void shouldCastSavedEventAppearWhenCastEmpty() {
        fixture.given(new CastCreatedEvent(castId, movieId, externalMovieId))
                .when(new SaveCastCommand(castId, castDTO))
                .expectEvents(new CastSavedEvent(castId, castDTO))
                .expectState(state -> {
                    var cast = state.getCast();
                    assertThat(cast.size()).isEqualTo(1);
                    var actor = cast.get(0);
                    assertThat(actor.getCastId()).isEqualTo("568");
                    assertThat(actor.getId()).isEqualTo("678");
                    assertThat(actor.getCharacter()).isEqualTo("John");
                    assertThat(actor.getName()).isEqualTo("Elon Musk");
                    assertThat(actor.getGender()).isEqualTo((short)2);
                    assertThat(actor.getOrder()).isEqualTo(1);
                    assertThat(actor.getProfilePath()).isEqualTo("/elon.jpg");
                });
    }

    @Test
    public void shouldCastSavedEventAppearWhenCastIsAlreadySet() {
        fixture.given(new CastCreatedEvent(castId, movieId, externalMovieId),
                      new CastSavedEvent(castId, castDTO))
                .when(new SaveCastCommand(castId, castDTO))
                .expectNoEvents()
                .expectState(state -> {
                    var cast = state.getCast();
                    assertThat(cast.size()).isEqualTo(1);
                    var actor = cast.get(0);
                    assertThat(actor.getCastId()).isEqualTo("568");
                    assertThat(actor.getId()).isEqualTo("678");
                    assertThat(actor.getCharacter()).isEqualTo("John");
                    assertThat(actor.getName()).isEqualTo("Elon Musk");
                    assertThat(actor.getGender()).isEqualTo((short)2);
                    assertThat(actor.getOrder()).isEqualTo(1);
                    assertThat(actor.getProfilePath()).isEqualTo("/elon.jpg");
                });
    }

    @Test
    public void shouldCastDeletedEventAppear() {
        fixture.given(new CastCreatedEvent(castId, movieId, externalMovieId),
                new CastSavedEvent(castId, castDTO))
                .when(new DeleteCastCommand(castId))
                .expectEvents(
                        new CastDeletedEvent(castId))
                .expectMarkedDeleted();
    }
}
