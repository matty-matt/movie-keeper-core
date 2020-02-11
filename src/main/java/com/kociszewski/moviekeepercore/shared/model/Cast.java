package com.kociszewski.moviekeepercore.shared.model;

import lombok.Data;

import java.util.List;

@Data
public class Cast {
    private String id;
    private List<CastInfo> cast;
}
