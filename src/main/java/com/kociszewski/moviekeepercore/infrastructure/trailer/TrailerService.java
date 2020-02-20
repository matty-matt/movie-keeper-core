package com.kociszewski.moviekeepercore.infrastructure.trailer;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class TrailerService {

    private final TrailerRepository trailerRepository;

    public void storeTrailers(String movieId, TrailerSectionDTO trailers) {
        trailers.getResults().forEach(trailer -> trailer.setMovieId(movieId));
        trailerRepository.insert(trailers.getResults());
    }

    public List<TrailerDTO> trailers(String movieId) {
        return trailerRepository.findByMovieId(movieId);
    }
}
