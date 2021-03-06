package com.kociszewski.moviekeeper.integration.common;

import com.kociszewski.moviekeeper.infrastructure.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class CommonIntegrationSetup {

    protected static final String SUPER_MOVIE = "SuperMovie";
    protected static final String GET_OR_POST_MOVIES = "http://localhost:%d/movies";
    protected static final String ALTER_MOVIE_URL = "http://localhost:%d/movies/%s";
    protected static final String ANOTHER_SUPER_MOVIE = "AnotherSuperMovie";
    protected static Date NOW;
    static ExternalMovie MOVIE;
    static ExternalMovie ANOTHER_MOVIE;

    @LocalServerPort
    protected int randomServerPort;
    @Autowired
    protected TestRestTemplate testRestTemplate;
    private List<MovieDTO> moviesToCleanAfterTests;

    @BeforeAll
    public static void beforeAll() {
        TestContainers.startAxonServer();
        TestContainers.startMongo();
    }

    @BeforeEach
    public void before() {
        moviesToCleanAfterTests = new ArrayList<>();
        NOW = new Date();
        MOVIE = generateExternalMovie(SUPER_MOVIE);
        ANOTHER_MOVIE = generateExternalMovie(ANOTHER_SUPER_MOVIE);
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
                .externalMovieId(externalMovieId)
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
                        .insertionDate(NOW)
                        .watched(false)
                        .build()).build();
    }

}
