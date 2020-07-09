package com.kociszewski.moviekeeper.infrastructure;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MovieDTO {
    @JsonProperty("id")
    private String aggregateId;
    private String externalMovieId;
    private String posterPath;
    private String title;
    private String originalTitle;
    private String overview;
    private String releaseDateDigital;
    private String releaseDate;
    private String originalLanguage;
    private double voteAverageMdb;
    private long voteCount;
    private int runtime;
    private List<GenreDTO> genres;
    private boolean watched;
    private Date creationDate;

    @JsonProperty("image")
    public String getPosterPath() {
        return posterPath;
    }
}
