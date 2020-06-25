package com.kociszewski.moviekeeper.infrastructure;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Document(collection = "movies")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MovieDTO {
    @Field("_id")
    @JsonProperty("id")
    @Id
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
    private Date lastRefreshDate;

    @JsonProperty("image")
    public String getPosterPath() {
        return posterPath;
    }
}
