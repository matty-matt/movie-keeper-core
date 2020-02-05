package com.kociszewski.moviekeepercore.infrastructure.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;

//TODO reactive repository?
public interface MovieRepository extends MongoRepository<MovieDTO, String> {
}
