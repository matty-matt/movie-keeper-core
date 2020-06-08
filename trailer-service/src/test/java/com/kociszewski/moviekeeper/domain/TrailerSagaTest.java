package com.kociszewski.moviekeeper.domain;

import com.kociszewski.moviekeeper.domain.commands.FetchTrailersCommand;
import com.kociszewski.moviekeeper.domain.commands.SaveTrailersCommand;
import com.kociszewski.moviekeeper.domain.events.TrailersDetailsEvent;
import com.kociszewski.moviekeeper.domain.events.TrailersCreatedEvent;
import com.kociszewski.moviekeeper.infrastructure.TrailerDTO;
import com.kociszewski.moviekeeper.infrastructure.TrailerSectionDTO;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.test.saga.SagaTestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.UUID;

public class TrailerSagaTest {

    private SagaTestFixture<TrailerSaga> fixture;
    private String movieId;
    private String externalMovieId;
    private String trailersId;
    private String proxyId;
    private TrailerSectionDTO trailerSectionDTO;

    @BeforeEach
    public void setup() {
        fixture = new SagaTestFixture<>(TrailerSaga.class);
        fixture.registerCommandGateway(CommandGateway.class);
        this.movieId = UUID.randomUUID().toString();
        this.proxyId = "proxy_".concat(movieId);
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
    public void shouldDispatchFetchTrailersDetailsCommand() {
        fixture.givenAggregate(trailersId)
                .published()
                .whenAggregate(trailersId)
                .publishes(new TrailersCreatedEvent(trailersId, movieId, externalMovieId))
                .expectActiveSagas(1)
                .expectDispatchedCommands(new FetchTrailersCommand(proxyId, externalMovieId, trailersId));
    }

    @Test
    public void shouldDispatchSaveTrailersCommand() {
        fixture.givenAggregate(trailersId)
                .published(new TrailersCreatedEvent(trailersId, movieId, externalMovieId))
                .whenAggregate(proxyId)
                .publishes(new TrailersDetailsEvent(proxyId, trailerSectionDTO))
                .expectActiveSagas(0)
                .expectDispatchedCommands(new SaveTrailersCommand(trailersId, trailerSectionDTO));
    }
}
