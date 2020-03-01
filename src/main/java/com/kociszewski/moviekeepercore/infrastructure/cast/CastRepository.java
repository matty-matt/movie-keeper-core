package com.kociszewski.moviekeepercore.infrastructure.cast;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CastRepository extends MongoRepository<CastDTO, String> {
    Optional<CastDTO> findByExternalMovieId(String externalMovieId);
    Optional<CastDTO> findByMovieId(String movieId);
}
