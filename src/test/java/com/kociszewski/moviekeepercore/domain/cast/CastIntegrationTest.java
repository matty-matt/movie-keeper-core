package com.kociszewski.moviekeepercore.domain.cast;

import com.kociszewski.moviekeepercore.domain.CommonIntegrationSetup;
import com.kociszewski.moviekeepercore.domain.ExternalService;
import com.kociszewski.moviekeepercore.infrastructure.cast.CastDTO;
import com.kociszewski.moviekeepercore.infrastructure.cast.CastInfoDTO;
import com.kociszewski.moviekeepercore.infrastructure.movie.MovieDTO;
import com.kociszewski.moviekeepercore.infrastructure.movie.NotFoundInExternalServiceException;
import com.kociszewski.moviekeepercore.infrastructure.trailer.TrailerSectionDTO;
import com.kociszewski.moviekeepercore.shared.model.ExternalMovie;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class CastIntegrationTest extends CommonIntegrationSetup {

    @Test
    public void shouldRetrieveCast() throws NotFoundInExternalServiceException {
        // given
        ExternalMovie generatedMovie = generateExternalMovie(SUPER_MOVIE);
        when(externalService.searchMovie(any())).thenReturn(generatedMovie);
        CastDTO castDTO = generateCast(generatedMovie.getExternalMovieId().getId());
        when(externalService.retrieveCast(any())).thenReturn(castDTO);
        TrailerSectionDTO trailerSectionDTO = generateTrailers(generatedMovie.getExternalMovieId().getId());
        when(externalService.retrieveTrailers(any())).thenReturn(trailerSectionDTO);

        ResponseEntity<MovieDTO> storedMovieResponse = storeMovie(SUPER_MOVIE);
        String movieId = Objects.requireNonNull(storedMovieResponse.getBody()).getAggregateId();

        // when
        ResponseEntity<CastInfoDTO[]> castResponse = testRestTemplate
                .getForEntity(
                        String.format("http://localhost:%d/movies/%s/cast", randomServerPort, movieId),
                        CastInfoDTO[].class);
        // then
        assertThat(castResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(castResponse.getBody()).isNotNull();

        List<CastInfoDTO> castInfos = Arrays.asList(castResponse.getBody());

        assertThat(castInfos.size()).isEqualTo(2);
        assertThat(castInfos).isEqualTo(castDTO.getCast());
    }
}
