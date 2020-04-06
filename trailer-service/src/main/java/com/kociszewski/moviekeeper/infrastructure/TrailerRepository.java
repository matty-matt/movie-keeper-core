package com.kociszewski.moviekeeper.infrastructure;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;


public interface TrailerRepository extends MongoRepository<TrailerSectionDTO, String> {
    Optional<TrailerSectionDTO> findByExternalMovieId(String externalMovieId);
    Optional<TrailerSectionDTO> findByMovieId(String movieId);
}
