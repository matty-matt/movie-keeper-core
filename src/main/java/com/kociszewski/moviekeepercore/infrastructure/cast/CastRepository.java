package com.kociszewski.moviekeepercore.infrastructure.cast;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface CastRepository extends MongoRepository<CastDTO, String> {
}
