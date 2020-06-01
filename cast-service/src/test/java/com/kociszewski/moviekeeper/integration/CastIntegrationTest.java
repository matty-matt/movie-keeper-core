package com.kociszewski.moviekeeper.integration;

import com.kociszewski.moviekeeper.domain.commands.FindCastCommand;
import com.kociszewski.moviekeeper.infrastructure.CastInfoDTO;
import com.kociszewski.moviekeeper.infrastructure.CastRepository;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.awaitility.Durations.FIVE_SECONDS;
import static org.awaitility.Durations.ONE_HUNDRED_MILLISECONDS;

public class CastIntegrationTest extends CommonIntegrationSetup {

    private static final String GET_CAST_URL = "http://localhost:%d/movies/%s/cast";

    @Autowired
    private CastRepository castRepository;

    @Autowired
    private CommandGateway commandGateway;

    private String movieId;

    @BeforeEach
    public void beforeEach() {
        this.movieId = "123";
        this.cast = generateCast(movieId);
    }

    @Test
    public void shouldRetrieveCast() {
        // given
        commandGateway.send(new FindCastCommand(cast.getAggregateId(), cast.getExternalMovieId(), cast.getMovieId()));

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

        List<CastInfoDTO> castDTOS = Arrays.asList(castResponse.getBody());

        assertThat(castDTOS.size()).isEqualTo(2);
        assertThat(castDTOS).isEqualTo(cast.getCast());
    }
}
