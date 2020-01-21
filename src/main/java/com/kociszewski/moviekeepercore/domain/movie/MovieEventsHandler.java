package com.kociszewski.moviekeepercore.domain.movie;

import com.kociszewski.moviekeepercore.domain.ExternalService;
import com.kociszewski.moviekeepercore.domain.movie.events.MovieQueriedForSearchEvent;
import lombok.RequiredArgsConstructor;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MovieEventsHandler {
    private final ExternalService externalService;

    @EventHandler
    public void on(MovieQueriedForSearchEvent event) {
        externalService.findMovie(event.getTitle());
    }
}