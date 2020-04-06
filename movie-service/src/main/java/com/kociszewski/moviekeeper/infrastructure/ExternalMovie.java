package com.kociszewski.moviekeeper.infrastructure;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class ExternalMovie {
    private String externalMovieId;
    private ExternalMovieInfo externalMovieInfo;
    private String digitalRelease;
    private MovieState movieState;
}
