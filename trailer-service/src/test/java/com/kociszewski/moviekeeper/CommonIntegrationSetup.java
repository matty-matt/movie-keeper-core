package com.kociszewski.moviekeeper;

import com.kociszewski.moviekeeper.infrastructure.TrailerDTO;
import com.kociszewski.moviekeeper.infrastructure.TrailerSectionDTO;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.util.Arrays;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class CommonIntegrationSetup {

    static final GenericContainer mongo;
    static final GenericContainer axonServer;
    private static final int MONGO_PORT = 29019;
    private static final int AXON_HTTP_PORT = 8024;
    private static final int AXON_GRPC_PORT = 8124;

    static {
        // These containers should be started only once during whole test suite
        axonServer = new GenericContainer("axoniq/axonserver:latest")
                .withExposedPorts(AXON_HTTP_PORT, AXON_GRPC_PORT)
                .waitingFor(
                        Wait.forLogMessage(".*Started AxonServer.*", 1)
                );
        axonServer.start();

        mongo = new GenericContainer("mongo:latest")
                .withExposedPorts(MONGO_PORT)
                .withEnv("MONGO_INITDB_DATABASE", "moviekeeper")
                .withCommand(String.format("mongod --port %d", MONGO_PORT))
                .waitingFor(
                        Wait.forLogMessage(".*waiting for connections.*", 1)
                );
        mongo.start();

        System.setProperty("ENV_MONGO_PORT", String.valueOf(mongo.getMappedPort(MONGO_PORT)));
        System.setProperty("ENV_AXON_GRPC_PORT", String.valueOf(axonServer.getMappedPort(AXON_GRPC_PORT)));
    }

    protected TrailerSectionDTO trailers;
    @LocalServerPort
    protected int randomServerPort;
    @Autowired
    protected TestRestTemplate testRestTemplate;

    protected TrailerSectionDTO generateTrailers(String movieId) {
        return TrailerSectionDTO.builder()
                .externalMovieId(UUID.randomUUID().toString())
                .aggregateId(UUID.randomUUID().toString())
                .movieId(movieId)
                .trailers(Arrays.asList(
                        TrailerDTO.builder()
                                .language("en")
                                .country("US")
                                .key("asd")
                                .name("First trailer")
                                .site("YouTube")
                                .size(1080)
                                .type("Teaser")
                                .build(),
                        TrailerDTO.builder()
                                .language("en")
                                .country("US")
                                .key("qwe")
                                .name("Second trailer")
                                .site("YouTube")
                                .size(1080)
                                .type("Teaser").
                                build()))
                .build();
    }

}
