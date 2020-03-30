package com.kociszewski.moviekeepercore.domain.cast;

import com.kociszewski.moviekeepercore.domain.CommonIntegrationSetup;
import com.kociszewski.moviekeepercore.infrastructure.cast.CastInfoDTO;
import com.kociszewski.moviekeepercore.infrastructure.movie.MovieDTO;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

public class CastIntegrationTestIT extends CommonIntegrationSetup {

    private static final String GET_CAST_URL = "http://localhost:%d/movies/%s/cast";

    @Test
    public void shouldRetrieveCast() {
        // given
        ResponseEntity<MovieDTO> storedMovieResponse = storeMovie(SUPER_MOVIE);
        String movieId = Objects.requireNonNull(storedMovieResponse.getBody()).getAggregateId();

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
