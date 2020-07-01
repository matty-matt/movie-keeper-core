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
        // Subscription query cannot exist without one @QueryHandler
        return Collections.emptyList();
    }
}
