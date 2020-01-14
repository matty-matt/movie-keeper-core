package com.kociszewski.moviekeepercore.domain.movie.info;

import lombok.Value;

@Value
public class Vote {
    private double voteAverageMdb;
    private long voteCount;
}
