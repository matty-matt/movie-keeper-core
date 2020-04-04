package com.kociszewski.trailerservice.infrastructure;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@Document(collection = "trailers")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrailerSectionDTO {

    @Field("_id")
    @JsonProperty("id")
    @Id
    private String aggregateId;
    private String movieId;
    private String externalMovieId;

    @Field("trailers")
    @JsonProperty("results")
    private List<TrailerDTO> trailers;
}
