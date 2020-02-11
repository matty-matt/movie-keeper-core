package com.kociszewski.moviekeepercore.infrastructure.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface MovieRepository extends MongoRepository<MovieDTO, String> {
}
