package com.kociszewski.moviekeepercore.infrastructure.cast;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
@Slf4j
public class CastService {

    private final CastRepository castRepository;

    public void storeCast(CastDTO cast) {
        castRepository.findByExternalMovieId(cast.getExternalMovieId()).ifPresentOrElse(
                foundCast -> skip(foundCast.getExternalMovieId()),
                () -> persistCast(cast)
        );
    }

    private void skip(String movieId) {
        log.info("Skipping persisting cast for movieId={}", movieId);
    }

    private void persistCast(CastDTO cast) {
        castRepository.insert(cast);
    }

    public CastDTO movieCast(String movieId) {
        return castRepository.findById(movieId).orElseThrow(CastNotFoundException::new);
    }

    public void deleteMovieCast(String movieId) {
        castRepository.deleteById(movieId);
    }
}
