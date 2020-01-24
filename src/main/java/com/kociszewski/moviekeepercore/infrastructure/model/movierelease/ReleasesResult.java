package com.kociszewski.moviekeepercore.infrastructure.model.movierelease;

import lombok.Data;

import java.util.List;

@Data
public class ReleasesResult {
    private List<CountryRelease> results;
}
