package com.kociszewski.moviekeepercore.domain.cast.queries;

import com.kociszewski.moviekeepercore.shared.model.MovieId;
import lombok.Value;

@Value
public class GetCastQuery {
    private MovieId movieId;
}
