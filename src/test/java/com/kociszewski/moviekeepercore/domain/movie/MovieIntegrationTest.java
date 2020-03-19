package com.kociszewski.moviekeepercore.domain.movie;

import com.kociszewski.moviekeepercore.domain.CommonIntegrationSetup;
import com.kociszewski.moviekeepercore.domain.ExternalService;
import com.kociszewski.moviekeepercore.infrastructure.movie.MovieDTO;
import com.kociszewski.moviekeepercore.infrastructure.movie.MovieRepository;
import com.kociszewski.moviekeepercore.infrastructure.movie.NotFoundInExternalServiceException;
import com.kociszewski.moviekeepercore.infrastructure.movie.TitleBody;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Date;
import java.util.Optional;

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
        when(externalService.searchMovie(any(), any())).thenReturn(mockedMovie);
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

        Optional<MovieDTO> persistedMovie = movieRepository.findByExternalMovieId(EXTERNAL_MOVIE_ID);
        persistedMovie.ifPresent(movie -> {
            assertThat(movie).isEqualTo(body);
            assertThat(movie.getCreationDate()).isEqualTo(now);
            assertThat(movie.getLastRefreshDate()).isEqualTo(now);
            assertThat(movie.isWatched()).isFalse();
        });

    }
}


