package com.kociszewski.moviekeepercore.domain.cast;


import lombok.Value;

import java.util.List;

@Value
public class Cast {
    private String movieId;
    private List<CastInfo> castInfo;
}
