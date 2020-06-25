package com.kociszewski.moviekeeper.infrastructure;

import com.kociszewski.moviekeeper.domain.queries.GetAllMoviesQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
@Slf4j
public class MovieProjection {

    private final MovieRepository movieRepository;

    @QueryHandler
    public List<MovieDTO> handle(GetAllMoviesQuery getAllMoviesQuery) {
        return movieRepository.findAll();
    }
}
