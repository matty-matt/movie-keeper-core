package com.kociszewski.moviekeepercore.infrastructure.trailer;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TrailerRepository extends MongoRepository<TrailerDTO, String> {
    List<TrailerDTO> findByMovieId(String movieId);

    void deleteByMovieId(String movieId);
}
