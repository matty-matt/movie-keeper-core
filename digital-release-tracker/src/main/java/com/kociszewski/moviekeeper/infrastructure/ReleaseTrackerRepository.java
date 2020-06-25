package com.kociszewski.moviekeeper.infrastructure;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ReleaseTrackerRepository extends MongoRepository<MovieDTO, String> {
    List<MovieDTO> findExternalMovieIdByWatchedFalse();
    List<MovieDTO> findAllByWatchedFalse();
}
