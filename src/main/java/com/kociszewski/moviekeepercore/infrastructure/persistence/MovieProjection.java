package com.kociszewski.moviekeepercore.infrastructure.persistence;

import com.kociszewski.moviekeepercore.domain.movie.queries.FindMovieQuery;
import org.axonframework.queryhandling.QueryHandler;

public class MovieProjection {
    // TODO repository
    // private Repository<MovieSth?> repository

    @QueryHandler
    public Void handle(FindMovieQuery findMovieQuery) {
//        return repository.movie(fetchMovieQuery.getMovieId());
        return null;
    }
}
