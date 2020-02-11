package com.kociszewski.moviekeepercore.shared.model;

import lombok.Data;

@Data
public class Trailer {
    private String id;
    private String movieId;
    private String language;
    private String country;
    private String key;
    private String name;
    private String site;
    private long size;
    private String type;
}
