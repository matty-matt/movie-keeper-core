package com.kociszewski.moviekeepercore.infrastructure.cast;

import com.kociszewski.moviekeepercore.shared.model.Cast;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class CastService {

    private final CastRepository castRepository;

    public void storeCast(Cast cast) {
    }
}
