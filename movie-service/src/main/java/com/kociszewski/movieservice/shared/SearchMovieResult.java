package com.kociszewski.movieservice.shared;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class SearchMovieResult {
    private int page;
    private int totalResults;
    private int totalPages;
    private List<ExternalMovieId> results;
}
