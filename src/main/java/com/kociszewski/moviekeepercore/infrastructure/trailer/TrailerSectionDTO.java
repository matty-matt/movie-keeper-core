package com.kociszewski.moviekeepercore.infrastructure.trailer;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@Document(collection = "trailers")
public class TrailerSectionDTO {

    @Field("_id")
    @JsonProperty("id")
    @Id
    private String aggregateId;
    private String externalMovieId;

    @Field("trailers")
    @JsonProperty("results")
    private List<TrailerDTO> trailers;
}
