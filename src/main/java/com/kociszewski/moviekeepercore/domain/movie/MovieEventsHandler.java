package com.kociszewski.moviekeepercore.domain.movie;

import com.kociszewski.moviekeepercore.domain.ExternalService;
import com.kociszewski.moviekeepercore.domain.movie.commands.SetExternalMovieIdCommand;
import com.kociszewski.moviekeepercore.domain.movie.events.SearchDelegatedEvent;
import com.kociszewski.moviekeepercore.shared.model.ExternalMovieId;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MovieEventsHandler {
    private final ExternalService externalService;
    private final CommandGateway commandGateway;

    @EventHandler
    public void on(SearchDelegatedEvent event) {
        externalService.searchMovie(event.getSearchPhrase(), event.getMovieId());
        commandGateway.sendAndWait(new SetExternalMovieIdCommand(event.getMovieId(), new ExternalMovieId("72389")));
    }
}
