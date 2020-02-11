package com.kociszewski.moviekeepercore.infrastructure.cast;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "cast")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CastDTO {
    private String id;
    private List<CastInfoDTO> cast;
}
