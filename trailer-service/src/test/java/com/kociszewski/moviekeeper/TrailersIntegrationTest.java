package com.kociszewski.moviekeeper;

import com.kociszewski.moviekeeper.domain.events.TrailersSavedEvent;
import com.kociszewski.moviekeeper.infrastructure.TrailerDTO;
import com.kociszewski.moviekeeper.infrastructure.TrailerRepository;
import org.axonframework.eventhandling.gateway.EventGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.awaitility.Durations.FIVE_SECONDS;
import static org.awaitility.Durations.ONE_HUNDRED_MILLISECONDS;

public class TrailersIntegrationTest extends CommonIntegrationSetup {

    private static final String GET_TRAILERS_URL = "http://localhost:%d/movies/%s/videos";

    @Autowired
    private TrailerRepository trailerRepository;

    @Autowired
    private EventGateway eventGateway;

    private String movieId;

    @BeforeEach
    public void beforeEach() {
        this.movieId = UUID.randomUUID().toString();
        this.trailers = generateTrailers(movieId);
    }

    @Test
    public void shouldRetrieveTrailers() {
        // given
        eventGateway.publish(new TrailersSavedEvent(trailers));

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
        assertThat(trailerDTOS).isEqualTo(trailers.getTrailers());
    }
}
