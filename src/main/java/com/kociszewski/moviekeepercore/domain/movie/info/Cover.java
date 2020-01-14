package com.kociszewski.moviekeepercore.domain.movie.info;

import lombok.Value;

@Value
public class Cover {
    private static final String IMAGE_HOST = "https://image.tmdb.org/t/p/w500/";
    private String coverUrl;
}
