package com.kociszewski.moviekeepercore.domain.movie.events;

import com.kociszewski.moviekeepercore.domain.movie.info.MovieId;
import com.kociszewski.moviekeepercore.domain.movie.info.SearchPhrase;
import lombok.Value;

@Value
public class SearchDelegatedEvent {
    private MovieId movieId;
    private SearchPhrase searchPhrase;
}
