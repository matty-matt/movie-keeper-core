package com.kociszewski.moviekeepercore.shared.model;

import lombok.Data;

import java.util.List;

@Data
public class ExternalTrailerSection {
    private String id;
    private List<ExternalTrailer> results;
}
