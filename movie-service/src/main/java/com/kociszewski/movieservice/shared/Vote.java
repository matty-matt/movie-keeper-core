package com.kociszewski.movieservice.shared;

import lombok.Value;

@Value
public class Vote {
    private double voteAverageMdb;
    private long voteCount;
}
