package com.kociszewski.moviekeepercore.domain.movie.events;

import com.kociszewski.moviekeepercore.domain.movie.info.MovieId;
import com.kociszewski.moviekeepercore.domain.movie.info.Title;
import lombok.Value;

@Value
public class MovieQueriedForSearchEvent {
    private MovieId movieId;
    private Title title;
}
