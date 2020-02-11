package com.kociszewski.moviekeepercore.infrastructure.cast;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CastInfoDTO {
    private String castId;
    private String character;
    private String creditId;
    private short gender;
    private String id;
    private String name;
    private int order;
    private String profilePath;
}

