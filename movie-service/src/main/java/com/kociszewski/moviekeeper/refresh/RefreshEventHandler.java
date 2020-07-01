package com.kociszewski.moviekeeper.refresh;

import com.kociszewski.moviekeeper.domain.commands.RefreshMovieCommand;
import com.kociszewski.moviekeeper.domain.events.MoviesRefreshedEvent;
import com.kociszewski.moviekeeper.domain.events.MultipleMoviesRefreshedEvent;
import com.kociszewski.moviekeeper.infrastructure.MovieDTO;
import com.kociszewski.moviekeeper.infrastructure.MovieProjection;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.gateway.EventGateway;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RefreshEventHandler {
    private final EventGateway eventGateway;
    private final MovieProjection movieProjection;
    private final CommandGateway commandGateway;

    @EventHandler
    public void handle(MultipleMoviesRefreshedEvent event) {
        List<MovieDTO> refreshedMovies = movieProjection.refreshMovies(event.getRefreshedMovies());
        eventGateway.publish(new MoviesRefreshedEvent(refreshedMovies));
        event.getRefreshedMovies()
                .parallelStream()
                .forEach(movie -> commandGateway.send(new RefreshMovieCommand(movie.getAggregateId(), movie)));
    }
}
