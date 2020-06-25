package com.kociszewski.moviekeeper.infrastructure;

import com.kociszewski.moviekeeper.domain.events.MoviesRefreshedEvent;
import com.kociszewski.moviekeeper.domain.queries.GetRefreshedMoviesQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
@Slf4j
public class ReleaseTrackerProjection {

    private final ReleaseTrackerRepository releaseTrackerRepository;
    private final QueryUpdateEmitter queryUpdateEmitter;

    @QueryHandler
    public List<MovieDTO> handle(GetRefreshedMoviesQuery query) {
        return releaseTrackerRepository.findAll();
    }

    @EventHandler
    public void handle(MoviesRefreshedEvent event) {
        queryUpdateEmitter.emit(GetRefreshedMoviesQuery.class, query -> true, event.getRefreshedMovies());
    }
}
