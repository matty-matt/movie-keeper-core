package com.kociszewski.moviekeepercore.domain.movie;

import com.kociszewski.moviekeepercore.domain.ExternalService;
import com.kociszewski.moviekeepercore.domain.movie.events.SearchDelegatedEvent;
import com.kociszewski.moviekeepercore.shared.model.ExternalMovieId;
import lombok.RequiredArgsConstructor;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MovieEventsHandler {
    private final ExternalService externalService;

    @EventHandler
    public void on(SearchDelegatedEvent event) {
        externalService.searchMovie(event.getSearchPhrase(), event.getMovieId());
    }
}
