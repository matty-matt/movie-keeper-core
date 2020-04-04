package com.kociszewski.trailerservice.domain.events;

import com.kociszewski.movieservice.infrastructure.trailer.TrailerSectionDTO;
import lombok.Value;

@Value
public class TrailersSavedEvent {
    private TrailerSectionDTO trailerSectionDTO;
}
