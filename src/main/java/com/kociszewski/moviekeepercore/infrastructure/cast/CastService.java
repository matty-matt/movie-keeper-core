package com.kociszewski.moviekeepercore.infrastructure.cast;

import com.kociszewski.moviekeepercore.shared.model.Cast;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CastService {

    private final CastRepository castRepository;

    public void storeCast(Cast cast) {
    }
}
