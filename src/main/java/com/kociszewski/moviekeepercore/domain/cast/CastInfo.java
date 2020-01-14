package com.kociszewski.moviekeepercore.domain.cast;

import lombok.Value;

@Value
public class CastInfo {
    //TODO fields
    private String castId;
    private String character;
    private String creditId;
    private short gender;
    private String id;
    private String name;
    private int order;
    private String profilePath;
}
