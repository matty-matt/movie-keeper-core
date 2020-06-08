package com.kociszewski.moviekeeper.infrastructure;

import com.kociszewski.moviekeeper.domain.commands.*;
import com.kociszewski.moviekeeper.domain.queries.GetAllMoviesQuery;
import com.kociszewski.moviekeeper.domain.queries.GetMovieQuery;
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

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@RestController
@RequiredArgsConstructor
@RequestMapping("/movies")
public class MovieController {

    private final CommandGateway commandGateway;
    private final QueryGateway queryGateway;

    @PostMapping
    public Mono<ResponseEntity<MovieDTO>> addMovieByTitle(@RequestBody TitleBody titleBody) {
        String movieId = UUID.randomUUID().toString();
        commandGateway.send(
                new CreateMovieCommand(
                        movieId,
                        titleBody.getTitle()));

        SubscriptionQueryResult<MovieDTO, MovieDTO> findMovieSubscription =
                queryGateway.subscriptionQuery(
                        new GetMovieQuery(movieId),
                        ResponseTypes.instanceOf(MovieDTO.class),
                        ResponseTypes.instanceOf(MovieDTO.class)
                );

        return findMovieSubscription.updates()
                .next()
                .map(movie -> mapResponse(
                        movie,
                        HttpStatus.CREATED,
                        (movieDTO) -> {
                            commandGateway.send(new DelegateTrailersAndCastSearchCommand(
                                    movieId,
                                    UUID.randomUUID().toString(),
                                    UUID.randomUUID().toString()));
                        }
                ))
                .doFinally(it -> findMovieSubscription.close());
    }

    @GetMapping("/{movieId}")
    public Mono<ResponseEntity<MovieDTO>> getMovieById(@PathVariable String movieId) {
        CompletableFuture<MovieDTO> future = queryGateway
                .query(new GetMovieQuery(movieId), MovieDTO.class);

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

    @PutMapping("/{movieId}")
    public Mono<ResponseEntity<MovieDTO>> updateMovieWatched(@PathVariable String movieId, @RequestBody WatchedBody watched) {
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

    @DeleteMapping("/{movieId}")
    public ResponseEntity<Void> deleteMovie(@PathVariable String movieId) {
        commandGateway.send(new DeleteMovieCommand(movieId));
        return ResponseEntity.noContent().build();
    }

    private ResponseEntity<MovieDTO> mapResponse(MovieDTO movie, HttpStatus onSuccessStatus) {
        return mapResponse(movie, onSuccessStatus, as -> {});
    }

    private ResponseEntity<MovieDTO> mapResponse(MovieDTO movie, HttpStatus onSuccessStatus, Consumer<MovieDTO> successfulCallback) {
        if (movie.getMovieState() != null) {
            switch (movie.getMovieState()) {
                case ALREADY_ADDED:
                    throw new MovieAlreadyAddedException("Movie with this title is already on your list.");
                case NOT_FOUND:
                    throw new MovieNotFoundException("Movie with this id not found.");
                case NOT_FOUND_IN_EXTERNAL_SERVICE:
                    throw new MovieNotFoundException("Movie with this title not found.");
            }
        } else {
            successfulCallback.accept(movie);
        }
        return new ResponseEntity<>(movie, onSuccessStatus);
    }
}
