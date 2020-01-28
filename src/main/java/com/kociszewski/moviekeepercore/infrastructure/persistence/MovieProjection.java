package com.kociszewski.moviekeepercore.infrastructure.persistence;

import com.kociszewski.moviekeepercore.domain.movie.queries.FindMovieQuery;
import com.kociszewski.moviekeepercore.infrastructure.exception.MovieNotFoundException;
import lombok.RequiredArgsConstructor;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class MovieProjection {

    private final MovieRepository movieRepository;

    @QueryHandler
    public MovieDTO handle(FindMovieQuery findMovieQuery) {
        return movieRepository.
                findById(findMovieQuery.getExternalMovieId().getId())
                .orElseThrow(() -> new MovieNotFoundException(
                        String.format("Movie with id=%s not found.", findMovieQuery.getExternalMovieId().getId())));
    }
}
