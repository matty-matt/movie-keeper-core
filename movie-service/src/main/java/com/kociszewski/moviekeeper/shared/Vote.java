package com.kociszewski.moviekeeper.shared;

import lombok.Value;

@Value
public class Vote {
    private double voteAverageMdb;
    private long voteCount;
}
