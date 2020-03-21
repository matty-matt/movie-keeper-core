package com.kociszewski.moviekeepercore.domain;

import com.kociszewski.moviekeepercore.infrastructure.cast.CastDTO;
import com.kociszewski.moviekeepercore.infrastructure.cast.CastInfoDTO;
import com.kociszewski.moviekeepercore.infrastructure.movie.MovieDTO;
import com.kociszewski.moviekeepercore.infrastructure.movie.TitleBody;
import com.kociszewski.moviekeepercore.infrastructure.trailer.TrailerDTO;
import com.kociszewski.moviekeepercore.infrastructure.trailer.TrailerSectionDTO;
import com.kociszewski.moviekeepercore.shared.model.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.util.Arrays;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class CommonIntegrationSetup {

    private static final int MONGO_PORT = 29019;
    private static final int AXON_HTTP_PORT = 8024;
    private static final int AXON_GRPC_PORT = 8124;
    private static final String MOVIE_ID = UUID.randomUUID().toString();
    protected static Date now;

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

    @Before
    public void before() {
        now = new Date();
    }

    protected ResponseEntity<MovieDTO> storeMovie(String title) {
        return testRestTemplate
                .postForEntity(String.format("http://localhost:%d/movies", randomServerPort), new TitleBody(title), MovieDTO.class);
    }

    protected ExternalMovie generateExternalMovie(String title) {
        String externalMovieId = String.valueOf(new Random().nextInt(1000));
        return ExternalMovie.builder()
                .externalMovieId(ExternalMovieId.builder().id(externalMovieId).build())
                .digitalRelease("2020-12-11T00:00")
                .externalMovieInfo(ExternalMovieInfo.builder()
                        .id(externalMovieId)
                        .posterPath("https://image.com/123")
                        .title(title)
                        .originalTitle(title)
                        .overview("This movie is super.")
                        .releaseDate("2020-11-10")
                        .originalLanguage("en")
                        .voteAverage(10.0)
                        .voteCount(2389)
                        .runtime(120)
                        .genres(Arrays.asList(new Genre("1", "Sci-Fi"), new Genre("2", "Action")))
                        .insertionDate(now)
                        .lastRefreshDate(now)
                        .watched(false)
                        .build()).build();
    }

    protected TrailerSectionDTO generateTrailers(String externalMovieId) {
        return TrailerSectionDTO.builder()
                .externalMovieId(externalMovieId)
                .movieId(MOVIE_ID)
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

    protected CastDTO generateCast(String externalMovieId) {
        return CastDTO.builder()
                .movieId(MOVIE_ID)
                .externalMovieId(externalMovieId)
                .cast(Arrays.asList(
                        CastInfoDTO.builder()
                                .id("789")
                                .castId("1")
                                .character("John")
                                .gender((short) 0)
                                .name("Mike Smith")
                                .order(1)
                                .profilePath("/pic1.jpg")
                                .build(),
                        CastInfoDTO.builder()
                                .id("790")
                                .castId("2")
                                .character("Alice")
                                .gender((short) 1)
                                .name("Rebecca White")
                                .order(2)
                                .profilePath("/pic2.jpg")
                                .build()
                ))
                .build();
    }
}
