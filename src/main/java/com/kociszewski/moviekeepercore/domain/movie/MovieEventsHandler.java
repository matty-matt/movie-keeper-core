package com.kociszewski.moviekeepercore.domain.movie;

import com.kociszewski.moviekeepercore.domain.ExternalService;
import com.kociszewski.moviekeepercore.domain.movie.events.MovieIdFoundEvent;
import com.kociszewski.moviekeepercore.domain.movie.events.SearchDelegatedEvent;
import com.kociszewski.moviekeepercore.domain.movie.events.SetExternalMovieIdCommand;
import com.kociszewski.moviekeepercore.shared.model.ExternalMovieId;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.gateway.EventGateway;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MovieEventsHandler {
    private final ExternalService externalService;
//    private final EventGateway eventGateway;
    private final CommandGateway commandGateway;

    @EventHandler
    public void on(SearchDelegatedEvent event) {
        ExternalMovieId externalMovieId = externalService.searchMovie(event.getSearchPhrase());
//        eventGateway.publish(new MovieIdFoundEvent(event.getMovieId(), externalMovieId));
        commandGateway.send(new SetExternalMovieIdCommand(event.getMovieId(), externalMovieId));
    }
}
