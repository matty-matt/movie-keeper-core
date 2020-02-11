package com.kociszewski.moviekeepercore.domain.movie.events;

import com.kociszewski.moviekeepercore.shared.model.MovieId;
import com.kociszewski.moviekeepercore.shared.model.SearchPhrase;
import lombok.Value;

@Value
public class MovieSearchDelegatedEvent {
    private MovieId movieId;
    private SearchPhrase searchPhrase;
}
