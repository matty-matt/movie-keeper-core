package com.kociszewski.moviekeeper.tmdb;

import lombok.Data;

import java.util.List;

@Data
public class ReleasesResult {
    private List<CountryRelease> results;
}
