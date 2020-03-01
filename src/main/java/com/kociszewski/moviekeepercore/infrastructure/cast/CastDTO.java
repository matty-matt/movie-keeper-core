package com.kociszewski.moviekeepercore.infrastructure.cast;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@Document(collection = "cast")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CastDTO {
    @Field("_id")
    @JsonProperty("id")
    @Id
    private String aggregateId;
    private String movieId;
    private String externalMovieId;
    private List<CastInfoDTO> cast;
}
