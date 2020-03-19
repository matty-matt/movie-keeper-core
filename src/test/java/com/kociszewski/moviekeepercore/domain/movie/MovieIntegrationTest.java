package com.kociszewski.moviekeepercore.domain.movie;

import com.kociszewski.moviekeepercore.domain.CommonIntegrationSetup;
import com.kociszewski.moviekeepercore.domain.ExternalService;
import com.kociszewski.moviekeepercore.infrastructure.movie.MovieDTO;
import com.kociszewski.moviekeepercore.infrastructure.movie.NotFoundInExternalServiceException;
import com.kociszewski.moviekeepercore.infrastructure.movie.TitleBody;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class MovieIntegrationTest extends CommonIntegrationSetup {

    @MockBean
    private ExternalService externalService;

    @Test
    public void shouldStoreMovie() throws NotFoundInExternalServiceException {
        when(externalService.searchMovie(any(), any())).thenReturn(mockedMovie);
        ResponseEntity<MovieDTO> venom = testRestTemplate.postForEntity(String.format("http://localhost:%d/movies", randomServerPort), new TitleBody("Venom"), MovieDTO.class);
        assertThat(venom.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }
}


