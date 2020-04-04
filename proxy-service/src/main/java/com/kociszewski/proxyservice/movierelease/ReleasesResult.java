package com.kociszewski.proxyservice.movierelease;

import lombok.Data;

import java.util.List;

@Data
public class ReleasesResult {
    private List<CountryRelease> results;
}
