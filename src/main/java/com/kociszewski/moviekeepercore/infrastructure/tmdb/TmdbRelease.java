package com.kociszewski.moviekeepercore.infrastructure.tmdb;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class TmdbRelease {
    private String releaseDate;
    private int type;
}
