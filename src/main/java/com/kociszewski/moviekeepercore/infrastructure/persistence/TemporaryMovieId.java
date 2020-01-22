package com.kociszewski.moviekeepercore.infrastructure.persistence;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "temp")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TemporaryMovieId {
    private String aggregateId;
    private String externalMovieId;
}
