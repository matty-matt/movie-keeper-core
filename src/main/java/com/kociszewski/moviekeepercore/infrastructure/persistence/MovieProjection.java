package com.kociszewski.moviekeepercore.infrastructure.persistence;

import com.kociszewski.moviekeepercore.domain.movie.queries.FindMovieQuery;
import lombok.RequiredArgsConstructor;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class MovieProjection {

    private final MovieIdRepository movieIdRepository;

    @QueryHandler
    public TemporaryMovieId handle(FindMovieQuery findMovieQuery) {
        return movieIdRepository.
                findById(findMovieQuery.getMovieId().getId())
                .orElseThrow(IllegalArgumentException::new);
    }
}
