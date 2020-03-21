package com.kociszewski.moviekeepercore.domain.movie;

import com.kociszewski.moviekeepercore.domain.CommonIntegrationSetup;
import com.kociszewski.moviekeepercore.domain.ExternalService;
import com.kociszewski.moviekeepercore.infrastructure.movie.*;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class MovieIntegrationTest extends CommonIntegrationSetup {

    @MockBean
    private ExternalService externalService;

    @Autowired
    private MovieRepository movieRepository;

    @Test
    public void shouldStoreMovie() throws NotFoundInExternalServiceException {
        // given
        when(externalService.searchMovie(any())).thenReturn(mockedMovie);
        when(externalService.retrieveCast(any())).thenReturn(mockedCast);
        when(externalService.retrieveTrailers(any())).thenReturn(mockedTrailers);

        // when
        ResponseEntity<MovieDTO> responseEntity = testRestTemplate
                .postForEntity(String.format("http://localhost:%d/movies", randomServerPort), new TitleBody("SuperMovie"), MovieDTO.class);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        MovieDTO body = responseEntity.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getAggregateId()).isNotEmpty();

        Optional<MovieDTO> persistedMovie = movieRepository.findByExternalMovieId(externalMovieId);
        persistedMovie.ifPresent(movie -> {
            assertThat(movie).isEqualTo(body);
            assertThat(movie.getCreationDate()).isEqualTo(now);
            assertThat(movie.getLastRefreshDate()).isEqualTo(now);
            assertThat(movie.isWatched()).isFalse();
        });
    }

    @Test
    public void shouldMarkMovieAsWatched() throws NotFoundInExternalServiceException {
        // given
        when(externalService.searchMovie(any())).thenReturn(mockedMovie);
        when(externalService.retrieveCast(any())).thenReturn(mockedCast);
        when(externalService.retrieveTrailers(any())).thenReturn(mockedTrailers);

        ResponseEntity<MovieDTO> storedMovie = testRestTemplate
                .postForEntity(String.format("http://localhost:%d/movies", randomServerPort), new TitleBody("SuperMovie2"), MovieDTO.class);
        HttpEntity<WatchedBody> httpEntity = new HttpEntity<>(new WatchedBody(true));

        // when
        ResponseEntity<MovieDTO> updateResult = testRestTemplate
                .exchange(
                        String.format("http://localhost:%d/movies/%s", randomServerPort, Objects.requireNonNull(storedMovie.getBody()).getAggregateId()),
                        HttpMethod.PUT,
                        httpEntity,
                        MovieDTO.class);
        // then
        assertThat(updateResult.getStatusCode()).isEqualTo(HttpStatus.OK);
        MovieDTO response = updateResult.getBody();
        assertThat(response).isNotNull();

        Optional<MovieDTO> persistedMovie = movieRepository.findByExternalMovieId(externalMovieId);
        persistedMovie.ifPresent(movie -> assertThat(movie.isWatched()).isTrue());
    }
}


