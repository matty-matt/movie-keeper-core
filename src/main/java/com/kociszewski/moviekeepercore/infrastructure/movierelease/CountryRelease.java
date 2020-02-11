package com.kociszewski.moviekeepercore.infrastructure.movierelease;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.kociszewski.moviekeepercore.infrastructure.tmdb.TmdbRelease;
import lombok.Data;

import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CountryRelease {
    @JsonProperty("iso_3166_1")
    private String country;
    private List<TmdbRelease> releaseDates;
}
