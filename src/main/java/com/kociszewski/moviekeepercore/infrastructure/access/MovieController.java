package com.kociszewski.moviekeepercore.infrastructure.access;

import com.kociszewski.moviekeepercore.domain.movie.commands.FindMovieCommand;
import com.kociszewski.moviekeepercore.domain.movie.info.MovieId;
import com.kociszewski.moviekeepercore.domain.movie.queries.GetAllMoviesQuery;
import com.kociszewski.moviekeepercore.infrastructure.access.mappedexceptions.MovieAlreadyAddedException;
import com.kociszewski.moviekeepercore.shared.model.SearchPhrase;
import com.kociszewski.moviekeepercore.domain.movie.queries.FindMovieQuery;
import com.kociszewski.moviekeepercore.infrastructure.access.mappedexceptions.MovieNotFoundException;
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
