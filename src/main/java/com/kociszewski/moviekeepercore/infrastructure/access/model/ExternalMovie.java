package com.kociszewski.moviekeepercore.infrastructure.access.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.List;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Data
public class ExternalMovie {
    private String id;
    private String posterPath;
    private String title;
    private String originalTitle;
    private String overview;
    private String releaseDate;
    private String originalLanguage;
    private double voteAverage;
    private long voteCount;
    private int runtime;
    private List<ExternalGenre> genres;
}
