package com.kociszewski.moviekeeper.domain.info;

import lombok.Value;

@Value
public class Poster {
    private static final String IMAGE_HOST = "https://image.tmdb.org/t/p/w500/";
    private String posterUrl;
}
