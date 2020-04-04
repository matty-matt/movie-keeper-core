package com.kociszewski.proxyservice.shared;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class ExternalMovie {
    private ExternalMovieId externalMovieId;
    private ExternalMovieInfo externalMovieInfo;
    private String digitalRelease;
}
