package com.kociszewski.moviekeeper.domain.events;

import com.kociszewski.moviekeeper.infrastructure.MovieDTO;
import lombok.Value;

import java.util.List;

@Value
public class MoviesRefreshedEvent {
    List<MovieDTO> refreshedMovies;
}
