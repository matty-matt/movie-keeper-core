package com.kociszewski.moviekeepercore.domain.movie;

import com.kociszewski.moviekeepercore.domain.ExternalService;
import com.kociszewski.moviekeepercore.domain.movie.events.SearchDelegatedEvent;
import com.kociszewski.moviekeepercore.infrastructure.persistence.MovieIdRepository;
import com.kociszewski.moviekeepercore.infrastructure.persistence.TemporaryMovieId;
import com.kociszewski.moviekeepercore.shared.model.ExternalMovieId;
import lombok.RequiredArgsConstructor;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MovieEventsHandler {
    private final ExternalService externalService;
    private final MovieIdRepository movieIdRepository;

    @EventHandler
    public void on(SearchDelegatedEvent event) {
        ExternalMovieId externalMovieId = externalService.searchMovie(event.getSearchPhrase(), event.getMovieId());
        movieIdRepository.save(new TemporaryMovieId(event.getMovieId().getId(), externalMovieId.getId()));
    }
}
