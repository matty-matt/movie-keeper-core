package com.kociszewski.moviekeeper.shared;

import com.kociszewski.moviekeeper.infrastructure.MovieState;
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
