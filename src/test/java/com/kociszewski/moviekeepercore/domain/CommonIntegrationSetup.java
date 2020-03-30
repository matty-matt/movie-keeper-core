package com.kociszewski.moviekeepercore.domain;

import com.kociszewski.moviekeepercore.infrastructure.cast.CastDTO;
import com.kociszewski.moviekeepercore.infrastructure.cast.CastInfoDTO;
import com.kociszewski.moviekeepercore.infrastructure.movie.MovieDTO;
import com.kociszewski.moviekeepercore.infrastructure.movie.NotFoundInExternalServiceException;
import com.kociszewski.moviekeepercore.infrastructure.movie.TitleBody;
import com.kociszewski.moviekeepercore.infrastructure.trailer.TrailerDTO;
import com.kociszewski.moviekeepercore.infrastructure.trailer.TrailerSectionDTO;
import com.kociszewski.moviekeepercore.shared.model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.util.*;

import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class CommonIntegrationSetup {

    private static final int MONGO_PORT = 29019;
    private static final int AXON_HTTP_PORT = 8024;
    private static final int AXON_GRPC_PORT = 8124;
    protected static final String GET_OR_POST_MOVIES = "http://localhost:%d/movies";
    protected static final String ALTER_MOVIE_URL = "http://localhost:%d/movies/%s";
    protected static final String SUPER_MOVIE = "SuperMovie";
    protected static final String ANOTHER_SUPER_MOVIE = "AnotherSuperMovie";
    protected static Date now;
    protected ExternalMovie superMovie;
    protected ExternalMovie anotherSuperMovie;
    protected CastDTO superMovieCast;
    protected TrailerSectionDTO superMovieTrailers;
    protected CastDTO anotherSuperMovieCast;
    protected TrailerSectionDTO anotherSuperMovieTrailers;
    protected List<MovieDTO> moviesToCleanAfterTests;

    @LocalServerPort
    protected int randomServerPort;

    @Autowired
    protected TestRestTemplate testRestTemplate;

    @MockBean
    protected ExternalService externalService;

    static final GenericContainer mongo;
    static final GenericContainer axonServer;

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

    @BeforeEach
    public void before() throws NotFoundInExternalServiceException {
        moviesToCleanAfterTests = new ArrayList<>();
        now = new Date();
        superMovie = generateExternalMovie(SUPER_MOVIE);
        superMovieCast = generateCast(superMovie.getExternalMovieId().getId());
        superMovieTrailers = generateTrailers(superMovie.getExternalMovieId().getId());
        anotherSuperMovie = generateExternalMovie(ANOTHER_SUPER_MOVIE);
        anotherSuperMovieCast = generateCast(anotherSuperMovie.getExternalMovieId().getId());
        anotherSuperMovieTrailers = generateTrailers(anotherSuperMovie.getExternalMovieId().getId());

        when(externalService.searchMovie(new SearchPhrase(SUPER_MOVIE))).thenReturn(superMovie);
        when(externalService.searchMovie(new SearchPhrase(ANOTHER_SUPER_MOVIE))).thenReturn(anotherSuperMovie);
        when(externalService.retrieveCast(superMovie.getExternalMovieId()))
                .thenReturn(superMovieCast);
        when(externalService.retrieveTrailers(superMovie.getExternalMovieId()))
                .thenReturn(superMovieTrailers);
        when(externalService.retrieveCast(anotherSuperMovie.getExternalMovieId()))
                .thenReturn(anotherSuperMovieCast);
        when(externalService.retrieveTrailers(anotherSuperMovie.getExternalMovieId()))
                .thenReturn(anotherSuperMovieTrailers);
    }

    @AfterEach
    public void after() {
        moviesToCleanAfterTests.forEach(movie -> deleteMovie(movie.getAggregateId()));
    }

    protected ResponseEntity<MovieDTO> storeMovie(String title) {
        ResponseEntity<MovieDTO> responseMovie = testRestTemplate
                .postForEntity(String.format(GET_OR_POST_MOVIES, randomServerPort), new TitleBody(title), MovieDTO.class);
        moviesToCleanAfterTests.add(responseMovie.getBody());
        return responseMovie;
    }

    protected ResponseEntity<Void> deleteMovie(String movieId) {
        return testRestTemplate.exchange(
                String.format(ALTER_MOVIE_URL, randomServerPort, movieId),
                HttpMethod.DELETE,
                null,
                Void.class);
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
