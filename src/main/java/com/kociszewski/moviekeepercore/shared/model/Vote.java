package com.kociszewski.moviekeepercore.shared.model;

import lombok.Value;

@Value
public class Vote {
    private double voteAverageMdb;
    private long voteCount;
}
