package com.kociszewski.moviekeepercore.infrastructure.trailer;

import org.springframework.data.mongodb.repository.MongoRepository;


public interface TrailerRepository extends MongoRepository<TrailerSectionDTO, String> {
    TrailerSectionDTO findByMovieId(String movieId);
    void deleteByMovieId(String movieId);
}
