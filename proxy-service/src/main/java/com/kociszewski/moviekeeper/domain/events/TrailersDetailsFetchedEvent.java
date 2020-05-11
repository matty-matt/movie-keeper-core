package com.kociszewski.moviekeeper.domain.events;

import com.kociszewski.moviekeeper.infrastructure.TrailerSectionDTO;
import lombok.Value;

@Value
public class TrailersDetailsFetchedEvent {
     String proxyId;
     TrailerSectionDTO trailerSectionDTO;
}
