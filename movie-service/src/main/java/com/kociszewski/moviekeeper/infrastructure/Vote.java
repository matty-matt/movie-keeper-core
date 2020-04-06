package com.kociszewski.moviekeeper.infrastructure;

import lombok.Value;

@Value
public class Vote {
    private double voteAverageMdb;
    private long voteCount;
}
