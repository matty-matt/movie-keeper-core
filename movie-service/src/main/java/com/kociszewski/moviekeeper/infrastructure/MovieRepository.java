package com.kociszewski.moviekeeper.infrastructure;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface MovieRepository extends MongoRepository<MovieDTO, String> {
    Optional<MovieDTO> findByExternalMovieId(String externalMovieId);
}
