package com.kociszewski.moviekeepercore.shared.model;

import lombok.Data;

@Data
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
