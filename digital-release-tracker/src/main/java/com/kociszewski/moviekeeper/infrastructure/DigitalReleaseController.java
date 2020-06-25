package com.kociszewski.moviekeeper.infrastructure;

import com.kociszewski.moviekeeper.domain.queries.GetAllMoviesQuery;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequiredArgsConstructor
@RequestMapping("/movies")
public class DigitalReleaseController {

    private final CommandGateway commandGateway;
    private final QueryGateway queryGateway;

    @GetMapping("/refresh")
    public Flux<MovieDTO> refreshMovies() {
        return Flux.fromIterable(queryGateway.query(
                new GetAllMoviesQuery(),
                ResponseTypes.multipleInstancesOf(MovieDTO.class)).join());
    }
}
