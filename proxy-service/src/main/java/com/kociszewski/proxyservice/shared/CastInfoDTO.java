package com.kociszewski.proxyservice.shared;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CastInfoDTO {
    private String castId;
    private String character;
    private short gender;
    private String id;
    private String name;
    private int order;
    private String profilePath;
}

