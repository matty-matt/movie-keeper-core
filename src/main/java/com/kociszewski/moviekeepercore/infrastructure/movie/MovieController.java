package com.kociszewski.moviekeepercore.infrastructure.movie;

import com.kociszewski.moviekeepercore.domain.movie.commands.FindMovieCommand;
import com.kociszewski.moviekeepercore.domain.movie.commands.UpdateMovieWatchedCommand;
import com.kociszewski.moviekeepercore.shared.model.Watched;
import com.kociszewski.moviekeepercore.shared.model.MovieId;
import com.kociszewski.moviekeepercore.domain.movie.queries.GetAllMoviesQuery;
import com.kociszewski.moviekeepercore.shared.model.SearchPhrase;
import com.kociszewski.moviekeepercore.domain.movie.queries.FindMovieQuery;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.SubscriptionQueryResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
        commandGateway.send(
                new FindMovieCommand(
                        aggregateId,
                        new SearchPhrase(titleBody.getTitle())));

        SubscriptionQueryResult<MovieDTO, MovieDTO> findMovieSubscription =
                queryGateway.subscriptionQuery(
                        new FindMovieQuery(aggregateId),
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
                .query(new FindMovieQuery(new MovieId(id)), MovieDTO.class);

        return Mono.fromFuture(future)
                .map(movie -> mapResponse(
                        movie,
                        HttpStatus.OK
                ));
    }

    @GetMapping
    public List<MovieDTO> getAllMovies() {
        // Or maybe ServerSentEvents by Spring WebFlux (preferably)
        return queryGateway.query(
                new GetAllMoviesQuery(),
                ResponseTypes.multipleInstancesOf(MovieDTO.class)).join();
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<MovieDTO>> updateMovieWatched(@PathVariable String id, @RequestBody WatchedBody watched) {
        MovieId movieId = new MovieId(id);
        commandGateway.send(new UpdateMovieWatchedCommand(movieId, new Watched(watched.isWatched())));

        SubscriptionQueryResult<MovieDTO, MovieDTO> updateMovieSubscription =
                queryGateway.subscriptionQuery(
                        new FindMovieQuery(movieId),
                        ResponseTypes.instanceOf(MovieDTO.class),
                        ResponseTypes.instanceOf(MovieDTO.class)
                );

        return updateMovieSubscription.updates()
                .next()
                .map(movie -> mapResponse(
                        movie,
                        HttpStatus.CREATED
                ))
                .doFinally(it -> updateMovieSubscription.close());
    }

    private ResponseEntity<MovieDTO> mapResponse(MovieDTO movie, HttpStatus onSuccessStatus) {
        if (movie.getMovieState() != null) {
            switch (movie.getMovieState()) {
                case ALREADY_ADDED:
                    throw new MovieAlreadyAddedException("Movie with this title is already on your list.");
                case NOT_FOUND:
                    throw new MovieNotFoundException("Movie with this id not found.");
                case EXTERNAL_SERVICE_NOT_FOUND:
                    throw new MovieNotFoundException("Movie with this title not found.");
            }
        }
        return new ResponseEntity<>(movie, onSuccessStatus);
    }
}
