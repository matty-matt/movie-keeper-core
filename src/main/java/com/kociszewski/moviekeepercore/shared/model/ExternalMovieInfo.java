package com.kociszewski.moviekeepercore.shared.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Builder
public class ExternalMovieInfo {
    private static final String IMAGE_HOST = "https://image.tmdb.org/t/p/w500";
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
    private List<Genre> genres;
    private Date insertionDate;
    private Date lastRefreshDate;

    public void setPosterPath(String posterPath) {
        this.posterPath = IMAGE_HOST.concat(Optional.ofNullable(posterPath).orElse("NULL"));
    }
}
