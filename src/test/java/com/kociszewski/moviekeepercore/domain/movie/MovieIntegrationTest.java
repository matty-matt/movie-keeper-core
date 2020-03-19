package com.kociszewski.moviekeepercore.domain.movie;

import com.kociszewski.moviekeepercore.domain.CommonIntegrationSetup;
import com.kociszewski.moviekeepercore.infrastructure.movie.MovieDTO;
import com.kociszewski.moviekeepercore.infrastructure.movie.TitleBody;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

public class MovieIntegrationTest extends CommonIntegrationSetup {

    @Test
    public void shouldStoreMovie() {
        ResponseEntity<MovieDTO> venom = testRestTemplate.postForEntity(String.format("http://localhost:%d/movies", randomServerPort), new TitleBody("Venom"), MovieDTO.class);
        assertThat(venom.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }
}