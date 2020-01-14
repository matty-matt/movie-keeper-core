package com.kociszewski.moviekeepercore.domain.movie.info;

import com.kociszewski.moviekeepercore.domain.movie.trailers.Trailer;

import java.util.List;

public class MovieInfo {
    private List<Trailer> trailers;
    private Cover cover;
    private Title title;
    private Title originalTitle;
    private Overview overview;
    private Language originalLanguage;
    private Vote vote;
    private Runtime runtime;
    private List<Genre> genres;
}
