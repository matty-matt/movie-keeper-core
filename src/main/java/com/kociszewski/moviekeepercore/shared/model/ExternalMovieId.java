package com.kociszewski.moviekeepercore.shared.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExternalMovieId {
    private String id;
}
