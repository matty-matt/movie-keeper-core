package com.kociszewski.moviekeeper.infrastructure;

import com.kociszewski.moviekeeper.domain.commands.CreateRefreshMoviesCommand;
import com.kociszewski.moviekeeper.domain.queries.GetRefreshedMoviesQuery;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.SubscriptionQueryResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/movies")
public class ReleaseTrackerController {

    private final CommandGateway commandGateway;
    private final QueryGateway queryGateway;

    @GetMapping("/refresh")
    public Mono<MovieDTO> refreshMovies() {
        commandGateway.send(
                new CreateRefreshMoviesCommand(UUID.randomUUID().toString()));

        SubscriptionQueryResult<MovieDTO, MovieDTO> refreshedMoviesSubscription =
                queryGateway.subscriptionQuery(
                        new GetRefreshedMoviesQuery(),
                        ResponseTypes.instanceOf(MovieDTO.class),
                        ResponseTypes.instanceOf(MovieDTO.class)
                );

        return refreshedMoviesSubscription.updates().next();
    }
}
