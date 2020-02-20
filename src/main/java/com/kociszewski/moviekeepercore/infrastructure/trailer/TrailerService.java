package com.kociszewski.moviekeepercore.infrastructure.trailer;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class TrailerService {

    private final TrailerRepository trailerRepository;

    public void storeTrailers(TrailerSectionDTO trailers) {
        trailerRepository.findById(trailers.getMovieId()).ifPresentOrElse(
                foundTrailers -> skip(foundTrailers.getMovieId()),
                () -> persistTrailers(trailers)
        );
    }

    private void skip(String movieId) {
        log.debug("Skipping persisting trailers for movieId={}", movieId);
    }

    private void persistTrailers(TrailerSectionDTO trailers) {
        trailerRepository.insert(trailers);
    }

    public TrailerSectionDTO trailers(String movieId) {
        return trailerRepository.findById(movieId).orElseThrow(TrailersNotFoundException::new);
    }
}
