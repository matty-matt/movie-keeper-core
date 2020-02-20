package com.kociszewski.moviekeepercore.infrastructure.trailer;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TrailerService {

    private final TrailerRepository trailerRepository;

    public void storeTrailers(TrailerSectionDTO trailers) {
        trailerRepository.insert(trailers);
    }

    public TrailerSectionDTO trailers(String movieId) {
        return trailerRepository.findByMovieId(movieId);
    }
}
