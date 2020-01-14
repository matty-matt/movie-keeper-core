package com.kociszewski.moviekeepercore.domain.movie.info.releases;

import lombok.Value;

@Value
public class Releases {
    private ReleaseDate digitalRelease;
    private ReleaseDate premiereRelease;
}
