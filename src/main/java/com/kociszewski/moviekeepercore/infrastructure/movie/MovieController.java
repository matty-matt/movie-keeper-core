package com.kociszewski.moviekeepercore.infrastructure.movie;

import com.kociszewski.moviekeepercore.domain.movie.commands.DeleteMovieCommand;
import com.kociszewski.moviekeepercore.domain.movie.commands.FindMovieCommand;
import com.kociszewski.moviekeepercore.domain.movie.commands.ToggleWatchedCommand;
import com.kociszewski.moviekeepercore.shared.model.*;
import com.kociszewski.moviekeepercore.domain.movie.queries.GetAllMoviesQuery;
import com.kociszewski.moviekeepercore.domain.movie.queries.GetMovieQuery;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.SubscriptionQueryResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
        MovieId aggregateId = new MovieId(UUID.randomUUID().toString());
        TrailerEntityId trailerEntityId = new TrailerEntityId(UUID.randomUUID().toString());
        CastEntityId castEntityId = new CastEntityId(UUID.randomUUID().toString());
        commandGateway.send(
                new FindMovieCommand(
                        aggregateId,
                        trailerEntityId,
                        castEntityId,
                        new SearchPhrase(titleBody.getTitle())));

        SubscriptionQueryResult<MovieDTO, MovieDTO> findMovieSubscription =
                queryGateway.subscriptionQuery(
                        new GetMovieQuery(aggregateId),
                        ResponseTypes.instanceOf(MovieDTO.class),
                        ResponseTypes.instanceOf(MovieDTO.class)
                );

        return findMovieSubscription.updates()
                .next()
                .map(movie -> mapResponse(
                        movie,
                        HttpStatus.CREATED
                ))
                .doFinally(it -> findMovieSubscription.close());
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<MovieDTO>> getMovieById(@PathVariable String id) {
        CompletableFuture<MovieDTO> future = queryGateway
                .query(new GetMovieQuery(new MovieId(id)), MovieDTO.class);

        return Mono.fromFuture(future)
                .map(movie -> mapResponse(
                        movie,
                        HttpStatus.OK
                ));
    }

    @GetMapping
    public Flux<MovieDTO> getAllMovies() {
        // Or maybe ServerSentEvents by Spring WebFlux (preferably)
        return Flux.fromIterable(queryGateway.query(
                new GetAllMoviesQuery(),
                ResponseTypes.multipleInstancesOf(MovieDTO.class)).join());
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<MovieDTO>> updateMovieWatched(@PathVariable String id, @RequestBody WatchedBody watched) {
        MovieId movieId = new MovieId(id);
        commandGateway.send(new ToggleWatchedCommand(movieId, new Watched(watched.isWatched())));

        SubscriptionQueryResult<MovieDTO, MovieDTO> updateMovieSubscription =
                queryGateway.subscriptionQuery(
                        new GetMovieQuery(movieId),
                        ResponseTypes.instanceOf(MovieDTO.class),
                        ResponseTypes.instanceOf(MovieDTO.class)
                );

        return updateMovieSubscription.updates()
                .next()
                .map(movie -> mapResponse(
                        movie,
                        HttpStatus.OK
                ))
                .doFinally(it -> updateMovieSubscription.close());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable String id) {
        MovieId movieId = new MovieId(id);
        commandGateway.sendAndWait(new DeleteMovieCommand(movieId));
        return ResponseEntity.noContent().build();
    }

    private ResponseEntity<MovieDTO> mapResponse(MovieDTO movie, HttpStatus onSuccessStatus) {
        if (movie.getMovieState() != null) {
            switch (movie.getMovieState()) {
                case ALREADY_ADDED:
                    throw new MovieAlreadyAddedException("Movie with this title is already on your list.");
                case NOT_FOUND:
                    throw new MovieNotFoundException("Movie with this id not found.");
                case NOT_FOUND_IN_EXTERNAL_SERVICE:
                    throw new MovieNotFoundException("Movie with this title not found.");
            }
        }
        return new ResponseEntity<>(movie, onSuccessStatus);
    }
}
