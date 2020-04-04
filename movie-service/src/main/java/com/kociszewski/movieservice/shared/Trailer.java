package com.kociszewski.movieservice.shared;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Trailer {
    private String language;
    private String country;
    private String key;
    private String name;
    private String site;
    private long size;
    private String type;
}
