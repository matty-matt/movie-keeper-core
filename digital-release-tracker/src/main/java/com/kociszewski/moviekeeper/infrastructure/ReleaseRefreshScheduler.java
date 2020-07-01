package com.kociszewski.moviekeeper.infrastructure;

import com.kociszewski.moviekeeper.domain.commands.CreateRefreshMoviesCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class ReleaseRefreshScheduler {

    private final CommandGateway commandGateway;

//    @Scheduled(initialDelay = 10000, fixedDelay = 60000)
//    public void refreshUnseenMovies() {
//        log.info("Refreshing not seen movies!");
//        commandGateway.send(
//                new CreateRefreshMoviesCommand(UUID.randomUUID().toString()));
//    }
}
