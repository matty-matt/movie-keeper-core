package com.kociszewski.moviekeepercore.domain;

import com.kociszewski.moviekeepercore.shared.model.ExternalMovie;
import com.kociszewski.moviekeepercore.shared.model.ExternalMovieId;
import com.kociszewski.moviekeepercore.shared.model.ExternalMovieInfo;
import com.kociszewski.moviekeepercore.shared.model.Genre;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.util.Arrays;
import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class CommonIntegrationSetup {

    private static final int MONGO_PORT = 29019;
    private static final int AXON_HTTP_PORT = 8024;
    private static final int AXON_GRPC_PORT = 8124;

    @LocalServerPort
    protected int randomServerPort;

    @Autowired
    protected TestRestTemplate testRestTemplate;

    @ClassRule
    public static GenericContainer mongo = new GenericContainer("mongo:latest")
            .withExposedPorts(MONGO_PORT)
            .withEnv("MONGO_INITDB_DATABASE", "moviekeeper")
            .withCommand(String.format("mongod --port %d", MONGO_PORT))
            .waitingFor(
                    Wait.forLogMessage(".*waiting for connections.*", 1)
            );

    @ClassRule
    public static GenericContainer axonServer = new GenericContainer("axoniq/axonserver:latest")
            .withExposedPorts(AXON_HTTP_PORT, AXON_GRPC_PORT)
            .waitingFor(
                    Wait.forLogMessage(".*Started AxonServer.*", 1)
            );

    @BeforeClass
    public static void beforeClass() {
        System.setProperty("ENV_MONGO_PORT", String.valueOf(mongo.getMappedPort(MONGO_PORT)));
        System.setProperty("ENV_AXON_GRPC_PORT", String.valueOf(axonServer.getMappedPort(AXON_GRPC_PORT)));
    }

    protected ExternalMovie mockedMovie = ExternalMovie.builder()
        .externalMovieId(ExternalMovieId.builder().id("123").build())
        .digitalRelease("2020-12-11T00:00")
        .externalMovieInfo(ExternalMovieInfo.builder()
                .id("123")
                .posterPath("https://image.com/123")
                .title("SuperMovie")
                .originalTitle("SuperMovie")
                .overview("This movie is super.")
                .releaseDate("2020-11-10")
                .originalLanguage("en")
                .voteAverage(10)
                .voteCount(2389)
                .runtime(120)
                .genres(Arrays.asList(new Genre("1", "Sci-Fi"), new Genre("2", "Action")))
                .insertionDate(new Date())
                .lastRefreshDate(new Date())
                .build()).build();
}
