package com.kociszewski.moviekeepercore.infrastructure.cast;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class CastService {

    private final CastRepository castRepository;

    public void storeCast(CastDTO cast) {
        castRepository.insert(cast);
    }

    public CastDTO movieCast(String movieId) {
        return castRepository.findById(movieId).orElseThrow(CastNotFoundException::new);
    }

    public void deleteMovieCast(String movieId) {
        castRepository.deleteById(movieId);
    }
}
