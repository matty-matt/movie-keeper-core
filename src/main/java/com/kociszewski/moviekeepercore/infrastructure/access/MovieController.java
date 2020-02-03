package com.kociszewski.moviekeepercore.infrastructure.access;

import com.kociszewski.moviekeepercore.domain.movie.commands.FindMovieCommand;
import com.kociszewski.moviekeepercore.domain.movie.info.MovieId;
import com.kociszewski.moviekeepercore.domain.movie.info.SearchPhrase;
import com.kociszewski.moviekeepercore.domain.movie.queries.FindMovieQuery;
import com.kociszewski.moviekeepercore.infrastructure.model.TitleBody;
import com.kociszewski.moviekeepercore.infrastructure.persistence.MovieDTO;
import com.kociszewski.moviekeepercore.shared.model.ExternalMovieId;
import lombok.RequiredArgsConstructor;
import org.axonframework.axonserver.connector.query.AxonServerRemoteQueryHandlingException;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.queryhandling.QueryGateway;
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
    public ResponseEntity<MovieId> addMovieByTitle(@RequestBody TitleBody titleBody) {
        MovieId movieId = new MovieId(UUID.randomUUID().toString());
        commandGateway.send(
                new FindMovieCommand(
                        movieId,
                        new SearchPhrase(titleBody.getTitle())));
        return new ResponseEntity<>(movieId, HttpStatus.CREATED);
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
