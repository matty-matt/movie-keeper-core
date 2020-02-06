package com.kociszewski.moviekeepercore.shared.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Document(collection = "trailers")
public class ExternalTrailer {
    @Field("_id")
    @JsonProperty("id")
    private String id;

    @JsonIgnore
    private String movieId;

    @JsonProperty("iso_639_1")
    private String language;

    @JsonProperty("iso_3166_1")
    private String country;
    private String key;
    private String name;
    private String site;
    private long size;
    private String type;
}

