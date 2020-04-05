package com.kociszewski.trailerservice.domain.events;

import com.kociszewski.trailerservice.infrastructure.TrailerSectionDTO;
import lombok.Value;

@Value
public class TrailersSavedEvent {
    private TrailerSectionDTO trailerSectionDTO;
}
