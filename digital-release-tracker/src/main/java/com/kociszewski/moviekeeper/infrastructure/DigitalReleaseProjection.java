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
public class DigitalReleaseProjection {

    private final DigitalReleaseRepository digitalReleaseRepository;
    private final QueryUpdateEmitter queryUpdateEmitter;

    @QueryHandler
    public List<MovieDTO> handle(GetRefreshedMoviesQuery query) {
        return digitalReleaseRepository.findAll();
    }

    @EventHandler
    public void handle(MoviesRefreshedEvent event) {
        if (event.getRefreshedMovies().isEmpty()) {
            log.info("stonks");
        } else {
            log.info("stonksy dwa");
            queryUpdateEmitter.emit(GetRefreshedMoviesQuery.class, query -> true, event.getRefreshedMovies());
        }
    }
}
