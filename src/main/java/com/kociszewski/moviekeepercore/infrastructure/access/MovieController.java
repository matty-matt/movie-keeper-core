package com.kociszewski.moviekeepercore.infrastructure.access;

import com.kociszewski.moviekeepercore.domain.movie.commands.FindMovieCommand;
import com.kociszewski.moviekeepercore.domain.movie.info.MovieId;
import com.kociszewski.moviekeepercore.domain.movie.info.SearchPhrase;
import com.kociszewski.moviekeepercore.domain.movie.queries.FindMovieQuery;
import com.kociszewski.moviekeepercore.infrastructure.exception.MovieNotFoundException;
import com.kociszewski.moviekeepercore.infrastructure.model.TitleBody;
import com.kociszewski.moviekeepercore.infrastructure.persistence.MovieDTO;
import lombok.RequiredArgsConstructor;
import org.axonframework.axonserver.connector.query.AxonServerRemoteQueryHandlingException;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.SubscriptionQueryResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public MovieDTO addMovieByTitle(@RequestBody TitleBody titleBody) {
        MovieId movieId = new MovieId(UUID.randomUUID().toString());
        commandGateway.send(
                new FindMovieCommand(
                        movieId,
                        new SearchPhrase(titleBody.getTitle())));

        SubscriptionQueryResult<MovieDTO, MovieDTO> subscriptionQueryResult =
                queryGateway.subscriptionQuery(
                        new FindMovieQuery(movieId),
                        ResponseTypes.instanceOf(MovieDTO.class),
                        ResponseTypes.instanceOf(MovieDTO.class)
                );
        return subscriptionQueryResult.updates().blockFirst();
        // Below is unreachable cuz AxonServerRemoteQueryHandlingException raises exception because of null emitted in MovieEventsHandler
//        if (movieDTO == null) {
//            throw new MovieNotFoundException("Takze tego");
//        } else {
//            return movieDTO;
//        }
    }

    @GetMapping("/{id}")
    public CompletableFuture<MovieDTO> getMovieById(@PathVariable String id) {
        return queryGateway
                .query(new FindMovieQuery(new MovieId(id)), MovieDTO.class);
    }

    @ExceptionHandler(AxonServerRemoteQueryHandlingException.class)
    public ResponseEntity<String> movieNotFound(AxonServerRemoteQueryHandlingException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @GetMapping
    public ResponseEntity<List<Object>> getAllMovies() {
        //TODO some kind of axon streaming to keep movies list always up to date?
        // https://docs.axoniq.io/reference-guide/implementing-domain-logic/query-handling/dispatching-queries#subscription-queries
        // Upper link shows subscriptions.
        // Or maybe ServerSentEvents by Spring WebFlux (preferably)
        return ResponseEntity.ok(Collections.emptyList());
    }
}
