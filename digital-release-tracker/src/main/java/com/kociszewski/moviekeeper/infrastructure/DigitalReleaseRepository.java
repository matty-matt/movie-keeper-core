package com.kociszewski.moviekeeper.infrastructure;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface DigitalReleaseRepository extends MongoRepository<MovieDTO, String> {
}
