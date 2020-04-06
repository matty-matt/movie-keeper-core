package com.kociszewski.moviekeeper.domain.commands;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class FetchCastCommand {
    @TargetAggregateIdentifier
    String proxyId;
    String externalMovieId;
    String castId;
}
