package com.kociszewski.moviekeepercore.infrastructure.movie;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.kociszewski.moviekeepercore.shared.model.ExternalGenre;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Document(collection = "movies")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MovieDTO {
    private String id;
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
    private List<ExternalGenre> genres;
    private boolean watched;
    private Date creationDate;
    private Date lastRefreshDate;

    @JsonIgnore
    private MovieState movieState;

    public MovieDTO(MovieState movieState) {
        this.movieState = movieState;
    }

    @JsonProperty("vote_average_mdb")
    public double getVoteAverageMdb() {
        return voteAverageMdb;
    }

    @JsonProperty("vote_average")
    public void setVoteAverageMdb(double voteAverageMdb) {
        this.voteAverageMdb = voteAverageMdb;
    }

    @JsonProperty("image")
    public String getPosterPath() {
        return posterPath;
    }
}
