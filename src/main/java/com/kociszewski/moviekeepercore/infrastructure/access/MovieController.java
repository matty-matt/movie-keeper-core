package com.kociszewski.moviekeepercore.infrastructure.access;

import com.kociszewski.moviekeepercore.domain.movie.commands.SearchMovieCommand;
import com.kociszewski.moviekeepercore.domain.movie.info.MovieId;
import com.kociszewski.moviekeepercore.domain.movie.info.Title;
import com.kociszewski.moviekeepercore.infrastructure.access.model.TitleBody;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/movies")
public class MovieController {

    private final CommandGateway commandGateway;

    @PostMapping
    public Mono<Void> addMovieByTitle(@RequestBody TitleBody titleBody) {
        String movieId = UUID.randomUUID().toString();
        commandGateway.send(new SearchMovieCommand(new MovieId(movieId), new Title(titleBody.getTitle())));
        //TODO Mono returning some value
        return Mono.empty();
    }

    @GetMapping
    public ResponseEntity<List<Object>> getAllMovies() {
        //TODO some kind of axon streaming to keep movies list always up to date?
        // maybe ServerSentEvents by Spring WebFlux
        return ResponseEntity.ok(Collections.emptyList());
    }
}
