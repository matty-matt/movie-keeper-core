package com.kociszewski.moviekeeper.domain;

import com.kociszewski.moviekeeper.domain.commands.FetchCastCommand;
import com.kociszewski.moviekeeper.domain.commands.SaveCastCommand;
import com.kociszewski.moviekeeper.domain.events.CastDetailsEvent;
import com.kociszewski.moviekeeper.domain.events.CastCreatedEvent;
import com.kociszewski.moviekeeper.infrastructure.CastDTO;
import com.kociszewski.moviekeeper.infrastructure.CastInfoDTO;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.test.saga.SagaTestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.UUID;

public class CastSagaTest {
    private SagaTestFixture<CastSaga> fixture;
    private String movieId;
    private String externalMovieId;
    private String castId;
    private String proxyId;
    private CastDTO castDTO;

    @BeforeEach
    public void setup() {
        fixture = new SagaTestFixture<>(CastSaga.class);
        fixture.registerCommandGateway(CommandGateway.class);
        this.movieId = UUID.randomUUID().toString();
        this.proxyId = "proxy_".concat(movieId);
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
    public void shouldDispatchFetchCastDetailsCommand() {
        fixture.givenAggregate(castId)
                .published()
                .whenAggregate(castId)
                .publishes(new CastCreatedEvent(castId, movieId, externalMovieId))
                .expectActiveSagas(1)
                .expectDispatchedCommands(new FetchCastCommand(proxyId, externalMovieId, castId));
    }

    @Test
    public void shouldDispatchSaveCastCommand() {
        fixture.givenAggregate(castId)
                .published(new CastCreatedEvent(castId, movieId, externalMovieId))
                .whenAggregate(proxyId)
                .publishes(new CastDetailsEvent(proxyId, castDTO))
                .expectActiveSagas(0)
                .expectDispatchedCommands(new SaveCastCommand(castId, castDTO));
    }
}
