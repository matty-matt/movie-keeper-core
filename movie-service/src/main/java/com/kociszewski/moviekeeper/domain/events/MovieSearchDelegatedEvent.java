package com.kociszewski.moviekeeper.domain.events;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class MovieSearchDelegatedEvent {
    String movieId;
    String searchPhrase;
}
