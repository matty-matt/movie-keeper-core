package com.kociszewski.moviekeepercore.domain.trailer;

import com.kociszewski.moviekeepercore.domain.CommonIntegrationSetup;
import com.kociszewski.moviekeepercore.infrastructure.cast.CastDTO;
import com.kociszewski.moviekeepercore.infrastructure.cast.CastInfoDTO;
import com.kociszewski.moviekeepercore.infrastructure.movie.MovieDTO;
import com.kociszewski.moviekeepercore.infrastructure.movie.NotFoundInExternalServiceException;
import com.kociszewski.moviekeepercore.infrastructure.trailer.TrailerDTO;
import com.kociszewski.moviekeepercore.infrastructure.trailer.TrailerSectionDTO;
import com.kociszewski.moviekeepercore.shared.model.ExternalMovie;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class TrailersIntegrationTest extends CommonIntegrationSetup {

    @Test
    public void shouldRetrieveTrailers() throws NotFoundInExternalServiceException {
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
        ResponseEntity<TrailerDTO[]> trailerResponse = testRestTemplate
                .getForEntity(
                        String.format("http://localhost:%d/movies/%s/videos", randomServerPort, movieId),
                        TrailerDTO[].class);
        // then
        assertThat(trailerResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(trailerResponse.getBody()).isNotNull();

        List<TrailerDTO> trailerDTOS = Arrays.asList(trailerResponse.getBody());

        assertThat(trailerDTOS.size()).isEqualTo(2);
        assertThat(trailerDTOS).isEqualTo(trailerSectionDTO.getTrailers());
    }
}
