package com.kociszewski.moviekeepercore.infrastructure.trailer;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TrailerDTO {

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

