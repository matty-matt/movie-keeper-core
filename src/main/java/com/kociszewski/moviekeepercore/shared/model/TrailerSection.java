package com.kociszewski.moviekeepercore.shared.model;

import lombok.Data;

import java.util.List;

@Data
public class TrailerSection {
    private String movieId;
    private List<Trailer> trailers;
}
