package com.kociszewski.moviekeeper.domain.events;

import com.kociszewski.moviekeeper.infrastructure.RefreshData;
import lombok.Value;

import java.util.List;

@Value
public class MoviesRefreshedEvent {
    List<RefreshData> refreshedMovies;
}
