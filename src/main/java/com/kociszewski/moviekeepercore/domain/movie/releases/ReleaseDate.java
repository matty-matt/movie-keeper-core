package com.kociszewski.moviekeepercore.domain.movie.releases;

import lombok.Value;

@Value
public class ReleaseDate {
    private Release digitalRelease;
    private Release premiereRelease;
}
