package com.kociszewski.moviekeeper.infrastructure;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RefreshMovie {
    private String externalMovieId;
    private String aggregateId;
}
