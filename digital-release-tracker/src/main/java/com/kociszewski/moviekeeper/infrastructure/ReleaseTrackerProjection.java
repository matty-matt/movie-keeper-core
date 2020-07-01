package com.kociszewski.moviekeeper.infrastructure;

import com.kociszewski.moviekeeper.domain.queries.GetRefreshedMoviesQuery;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class ReleaseTrackerProjection {
    @QueryHandler
    public List<MovieDTO> handle(GetRefreshedMoviesQuery query) {
        // For Axon because without it he thinks that is not handled
        return Collections.emptyList();
    }
}
