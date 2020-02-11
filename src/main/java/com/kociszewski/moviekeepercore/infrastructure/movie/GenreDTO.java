package com.kociszewski.moviekeepercore.infrastructure.movie;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class GenreDTO {
    private String id;
    private String name;
}
