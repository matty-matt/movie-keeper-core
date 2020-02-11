package com.kociszewski.moviekeepercore.infrastructure.movie;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface MovieRepository extends MongoRepository<MovieDTO, String> {
}
