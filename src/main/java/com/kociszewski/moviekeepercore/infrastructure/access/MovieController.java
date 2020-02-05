package com.kociszewski.moviekeepercore.infrastructure.access;

import com.kociszewski.moviekeepercore.domain.movie.commands.FindMovieCommand;
import com.kociszewski.moviekeepercore.domain.movie.info.MovieId;
import com.kociszewski.moviekeepercore.domain.movie.info.SearchPhrase;
import com.kociszewski.moviekeepercore.domain.movie.queries.FindMovieQuery;
import com.kociszewski.moviekeepercore.infrastructure.exception.MovieNotFoundException;
import com.kociszewski.moviekeepercore.infrastructure.model.TitleBody;
import com.kociszewski.moviekeepercore.infrastructure.persistence.MovieDTO;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.SubscriptionQueryResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
@RequestMapping("/movies")
public class MovieController {

    private final CommandGateway commandGateway;
    private final QueryGateway queryGateway;

    @PostMapping
    public Mono<ResponseEntity<MovieDTO>> addMovieByTitle(@RequestBody TitleBody titleBody) {
        MovieId movieId = new MovieId(UUID.randomUUID().toString());
        commandGateway.send(
                new FindMovieCommand(
                        movieId,
                        new SearchPhrase(titleBody.getTitle())));

        SubscriptionQueryResult<MovieDTO, MovieDTO> findMovieSubscription =
                queryGateway.subscriptionQuery(
                        new FindMovieQuery(movieId),
                        ResponseTypes.instanceOf(MovieDTO.class),
                        ResponseTypes.instanceOf(MovieDTO.class)
                );

        return findMovieSubscription.updates()
                .next()
                .map(movie -> mapResponse(
                        movie,
                        HttpStatus.CREATED,
                        String.format("Movie with title '%s' not found.", titleBody.getTitle()))
                )
                .doFinally(it -> findMovieSubscription.close());
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<MovieDTO>> getMovieById(@PathVariable String id) {
        CompletableFuture<MovieDTO> future = queryGateway
                .query(new FindMovieQuery(new MovieId(id)), MovieDTO.class);

        return Mono.fromFuture(future)
                .map(movie -> mapResponse(
                        movie,
                        HttpStatus.OK,
                        String.format("Movie with id=%s not found.", id)
                ));
    }

    @GetMapping
    public ResponseEntity<List<Object>> getAllMovies() {
        //TODO some kind of axon streaming to keep movies list always up to date?
        // https://docs.axoniq.io/reference-guide/implementing-domain-logic/query-handling/dispatching-queries#subscription-queries
        // Upper link shows subscriptions.
        // Or maybe ServerSentEvents by Spring WebFlux (preferably)
        return ResponseEntity.ok(Collections.emptyList());
    }

    private ResponseEntity<MovieDTO> mapResponse(MovieDTO movie, HttpStatus onSuccessStatus, String onErrorMessage) {
        if (movie.getExternalMovieId() == null) {
            throw new MovieNotFoundException(onErrorMessage);
        } else {
            return new ResponseEntity<>(movie, onSuccessStatus);
        }
    }
}
