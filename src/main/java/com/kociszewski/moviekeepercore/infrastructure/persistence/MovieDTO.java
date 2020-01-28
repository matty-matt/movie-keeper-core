package com.kociszewski.moviekeepercore.infrastructure.persistence;

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
@NoArgsConstructor
@AllArgsConstructor
public class MovieDTO {
    private String id;
    private String aggregateId;
    private String image;
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
}
