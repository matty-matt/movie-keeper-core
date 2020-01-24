package com.kociszewski.moviekeepercore.infrastructure.access;

import com.kociszewski.moviekeepercore.domain.movie.commands.FindMovieCommand;
import com.kociszewski.moviekeepercore.domain.movie.info.MovieId;
import com.kociszewski.moviekeepercore.domain.movie.info.SearchPhrase;
import com.kociszewski.moviekeepercore.infrastructure.model.TitleBody;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/movies")
public class MovieController {

    private final CommandGateway commandGateway;
    private final QueryGateway queryGateway;

    @PostMapping
    public MovieId addMovieByTitle(@RequestBody TitleBody titleBody) {
        MovieId movieId = new MovieId(UUID.randomUUID().toString());
        commandGateway.sendAndWait(
                new FindMovieCommand(
                        movieId,
                        new SearchPhrase(titleBody.getTitle())));

        return movieId;
        // TODO now another commands should be dispatched asynchronously
        //  [send instead of sendAndWait] (as they're just requests) based on fetched id
        // TODO examine if this request will not return empty json
//        return Mono.fromFuture(queryGateway.query(new FindMovieQuery(movieId), TemporaryMovieId.class));
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
