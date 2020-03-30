package com.kociszewski.moviekeepercore.domain.trailer;

import com.kociszewski.moviekeepercore.domain.CommonIntegrationSetup;
import com.kociszewski.moviekeepercore.infrastructure.movie.MovieDTO;
import com.kociszewski.moviekeepercore.infrastructure.trailer.TrailerDTO;
import com.kociszewski.moviekeepercore.infrastructure.trailer.TrailerRepository;
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

public class TrailersIntegrationTest extends CommonIntegrationSetup {

    private static final String GET_TRAILERS_URL = "http://localhost:%d/movies/%s/videos";

    @Autowired
    private TrailerRepository trailerRepository;

    @Test
    public void shouldRetrieveTrailers() {
        // given
        ResponseEntity<MovieDTO> storedMovieResponse = storeMovie(SUPER_MOVIE);
        String movieId = Objects.requireNonNull(storedMovieResponse.getBody()).getAggregateId();

        await()
                .atMost(FIVE_SECONDS)
                .with()
                .pollInterval(ONE_HUNDRED_MILLISECONDS)
                .until(() -> trailerRepository.findByMovieId(movieId).isPresent());

        // when
        ResponseEntity<TrailerDTO[]> trailerResponse = testRestTemplate
                .getForEntity(
                        String.format(GET_TRAILERS_URL, randomServerPort, movieId),
                        TrailerDTO[].class);
        // then
        assertThat(trailerResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(trailerResponse.getBody()).isNotNull();

        List<TrailerDTO> trailerDTOS = Arrays.asList(trailerResponse.getBody());

        assertThat(trailerDTOS.size()).isEqualTo(2);
        assertThat(trailerDTOS).isEqualTo(superMovieTrailers.getTrailers());
    }
}
