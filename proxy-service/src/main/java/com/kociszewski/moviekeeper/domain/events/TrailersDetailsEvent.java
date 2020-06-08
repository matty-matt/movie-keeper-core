package com.kociszewski.moviekeeper.domain.events;

import com.kociszewski.moviekeeper.infrastructure.TrailerSectionDTO;
import lombok.Value;

@Value
public class TrailersDetailsEvent {
     String proxyId;
     TrailerSectionDTO trailerSectionDTO;
}
