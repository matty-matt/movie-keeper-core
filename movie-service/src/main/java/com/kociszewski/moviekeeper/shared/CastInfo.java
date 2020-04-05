package com.kociszewski.moviekeeper.shared;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CastInfo {
    private String castId;
    private String character;
    private String creditId;
    private short gender;
    private String id;
    private String name;
    private int order;
    private String profilePath;
}
