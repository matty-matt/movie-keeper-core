package com.kociszewski.moviekeepercore.infrastructure.access;

import com.kociszewski.moviekeepercore.domain.movie.commands.SearchMovieCommand;
import com.kociszewski.moviekeepercore.domain.movie.info.MovieId;
import com.kociszewski.moviekeepercore.domain.movie.info.Title;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/movies")
public class MovieController {

    private final CommandGateway commandGateway;

    @PostMapping
    public ResponseEntity<Void> addMovieByTitle(@RequestBody TitleBody titleBody) {
        String movieId = UUID.randomUUID().toString();
        commandGateway.send(new SearchMovieCommand(new MovieId(movieId), new Title(titleBody.getTitle())));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
