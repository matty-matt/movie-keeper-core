package com.kociszewski.movieservice.external.proxy;

import lombok.*;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class FetchMovieDetailsCommand {
    @TargetAggregateIdentifier
    String proxyId;
    String searchPhrase;
}
