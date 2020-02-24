package com.kociszewski.moviekeepercore.domain.trailer.queries;

import com.kociszewski.moviekeepercore.shared.model.MovieId;
import lombok.Value;

@Value
public class GetTrailersQuery {
    private MovieId movieId;
}
