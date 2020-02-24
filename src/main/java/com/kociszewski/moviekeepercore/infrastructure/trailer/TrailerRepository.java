package com.kociszewski.moviekeepercore.infrastructure.trailer;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;


public interface TrailerRepository extends MongoRepository<TrailerSectionDTO, String> {
    Optional<TrailerSectionDTO> findByExternalMovieId(String externalMovieId);
}
