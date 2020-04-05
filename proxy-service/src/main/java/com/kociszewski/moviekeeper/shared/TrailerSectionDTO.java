package com.kociszewski.moviekeeper.shared;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrailerSectionDTO {

    private String aggregateId;
    private String movieId;
    private String externalMovieId;

    @JsonProperty("results")
    private List<TrailerDTO> trailers;
}
