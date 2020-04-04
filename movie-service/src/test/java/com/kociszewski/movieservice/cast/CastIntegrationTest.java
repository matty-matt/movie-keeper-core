package com.kociszewski.movieservice.cast;

import com.kociszewski.movieservice.CommonIntegrationSetup;
import com.kociszewski.movieservice.infrastructure.cast.CastInfoDTO;
import com.kociszewski.movieservice.infrastructure.cast.CastRepository;
import com.kociszewski.movieservice.infrastructure.MovieDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.awaitility.Durations.FIVE_SECONDS;
import static org.awaitility.Durations.ONE_HUNDRED_MILLISECONDS;

public class CastIntegrationTest extends CommonIntegrationSetup {

    private static final String GET_CAST_URL = "http://localhost:%d/movies/%s/cast";

    @Autowired
    private CastRepository castRepository;

    @Test
    public void shouldRetrieveCast() {
        // given
        ResponseEntity<MovieDTO> storedMovieResponse = storeMovie(SUPER_MOVIE);
        String movieId = Objects.requireNonNull(storedMovieResponse.getBody()).getAggregateId();

        await()
                .atMost(FIVE_SECONDS)
                .with()
                .pollInterval(ONE_HUNDRED_MILLISECONDS)
                .until(() -> castRepository.findByMovieId(movieId).isPresent());

        // when
        ResponseEntity<CastInfoDTO[]> castResponse = testRestTemplate
                .getForEntity(
                        String.format(GET_CAST_URL, randomServerPort, movieId),
                        CastInfoDTO[].class);

        // then
        assertThat(castResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(castResponse.getBody()).isNotNull();

        List<CastInfoDTO> castInfos = Arrays.asList(castResponse.getBody());

        assertThat(castInfos.size()).isEqualTo(2);
        assertThat(castInfos).isEqualTo(superMovieCast.getCast());
    }
}
